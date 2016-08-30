package com.iflytek.platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.AccessToken;
import com.iflytek.platform.utils.Tools;

import java.util.EnumMap;
import java.util.Map;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/30/16,20:45
 * @see
 */
public enum PlatformTokenKeeper {

    INSTANCE;

    private static final String SP_NAME = "platformTokens";
    private static final String SPILT = "<@>";

    private SharedPreferences sp;

    private EnumMap<ChannelType, AccessToken> tokenCache;

    PlatformTokenKeeper() {
        tokenCache = new EnumMap<>(ChannelType.class);
    }

    void init(Context context) {
        this.sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        loadConfig();
    }

    private void loadConfig() {
        Map<String, ?> map = sp.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            ChannelType channelType = ChannelType.convert(entry.getKey());
            AccessToken token = convertValue(String.valueOf(entry.getValue()));
            if (null != channelType && null != token) {
                tokenCache.put(channelType, token);
            }
        }
    }

    private void save() {
        SharedPreferences.Editor editor = sp.edit();
        for (Map.Entry<ChannelType, AccessToken> tokenEntry : tokenCache.entrySet()) {
            editor.putString(getKey(tokenEntry.getKey()), convertValue(tokenEntry.getValue()));
        }
        editor.apply();
    }

    public void setToken(ChannelType type, AccessToken token) {
        if (null == type) {
            return;
        }
        if (null == token) {
            removeToken(type);
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getKey(type), convertValue(token));
        editor.apply();
    }

    public AccessToken getToken(ChannelType type) {
        if (null == type) {
            return null;
        }
        return tokenCache.get(type);
    }

    public void removeToken(ChannelType type) {
        if (null == type) {
            return;
        }
        tokenCache.remove(type);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(getKey(type));
        editor.apply();
    }

    private String getKey(ChannelType type) {
        return String.valueOf(type);
    }

    private String convertValue(AccessToken token) {
        return token.getUid() + SPILT + token.getToken() + SPILT + token.getExpiresIn();
    }

    private AccessToken convertValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        String[] values = value.split(SPILT);
        if (3 == values.length) {
            return AccessToken.createToken(values[0], values[1], Tools.getLong(values[2], 0));
        } else {
            return null;
        }
    }

}
