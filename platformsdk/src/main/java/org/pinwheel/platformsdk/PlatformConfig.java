package org.pinwheel.platformsdk;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.utils.Tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/18,10:43
 * @see
 */
public enum PlatformConfig {

    INSTANCE;

    private enum Type {
        SINA,// 小写对应配置文件中的平台名称
        WEIXIN,
        TAOBAO,
        ALIPAY,
        TENCENT;

        private static Type convert(String name) {
            for (Type type : Type.values()) {
                if (type.toString().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final String CONFIG_FILE = "platforms.json";
    private static final String KEY_NAME = "name";
    private static final String KEY_ID = "id";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_REDIRECT_URL = "redirect_url";

    private EnumMap<Type, Map<String, String>> keyStore;

    PlatformConfig() {
        keyStore = new EnumMap<>(Type.class);
    }

    public void init(Context context) {
        // init native token
        PlatformTokenKeeper.INSTANCE.init(context);
        // load config
        loadAssetsConfig(context);
    }

    public String getWeixinId() {
        return getValue(Type.WEIXIN, KEY_ID);
    }

    public String getWeixinSecret() {
        return getValue(Type.WEIXIN, KEY_SECRET);
    }

    public String getTencentId() {
        return getValue(Type.TENCENT, KEY_ID);
    }

    public String getTencentKey() {
        return getValue(Type.TENCENT, KEY_SECRET);
    }

    public String getSinaKey() {
        return getValue(Type.SINA, KEY_ID);
    }

    public String getSinaSecret() {
        return getValue(Type.SINA, KEY_SECRET);
    }

    public String getSinaRedirectUrl() {
        return getValue(Type.SINA, KEY_REDIRECT_URL, Constants.DEFAULT_SINA_REDIRECT_URL);
    }

    public String getTaobaoKey() {
        return getValue(Type.TAOBAO, KEY_ID);
    }

    public String getTaobaoSecret() {
        return getValue(Type.TAOBAO, KEY_SECRET);
    }

    public String getAlipayKey() {
        return getValue(Type.ALIPAY, KEY_ID);
    }

    public String getAlipaySecret() {
        return getValue(Type.ALIPAY, KEY_SECRET);
    }

    private String getValue(Type type, String key) {
        return getValue(type, key, null);
    }

    private String getValue(Type type, String key, String defaultValue) {
        String value = null;
        if (keyStore.containsKey(type)) {
            Map<String, String> valueMap = keyStore.get(type);
            if (valueMap.containsKey(key)) {
                value = valueMap.get(key);
            } else {
                value = defaultValue;
            }
        }
        return null == value ? "" : value;
    }

    private void loadAssetsConfig(Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedReader br = null;
        try {
            InputStream inputStream = assetManager.open(CONFIG_FILE);
            br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            JSONObject json = new JSONObject(result.toString());
            final String version = json.getString("version");
            if (TextUtils.isEmpty(version) || version.startsWith("1.0")) {
                parseConfigBy10(json);
            } else {
                Log.e(Constants.TAG, "[PlatformConfig]: unknown 'platform.json' format version");
            }
        } catch (Exception e) {
            Toast.makeText(context, Constants.TAG + ":\n'platform.json' format error.", Toast.LENGTH_SHORT).show();
            Log.e(Constants.TAG, "[PlatformConfig]: platform.json format error.\n " + e.getMessage());
        } finally {
            Tools.close(br);
        }
    }

    private void parseConfigBy10(JSONObject json) throws Exception {
        JSONArray platforms = json.getJSONArray("platforms");
        final int size = platforms.length();
        keyStore.clear();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObj = platforms.getJSONObject(i);
            final String typeName = jsonObj.getString(KEY_NAME);
            Type type = Type.convert(typeName);
            if (null != type) {
                Map<String, String> values = new HashMap<>(jsonObj.length());
                Iterator<String> iterator = jsonObj.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (!TextUtils.isEmpty(key)) {
                        values.put(key, jsonObj.getString(key));
                    }
                }
                keyStore.put(type, values);
            }
        }
    }

}
