package org.pinwheel.platformsdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.pinwheel.platformsdk.entity.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/16/16,21:08
 * @see
 */
public final class Tools {

    private Tools() {
        throw new AssertionError();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getJsonString(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public static int getJsonInt(JSONObject json, String key, int defaultValue) {
        try {
            return (int) json.getDouble(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static boolean getJsonBoolean(JSONObject json, String key) {
        try {
            return json.getBoolean(key);
        } catch (JSONException e) {
            return false;
        }
    }

    public static long getLong(String value, long defaultVlaue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultVlaue;
        }
    }

    public static <T> T clone(T object) {
        T result = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(byteArrayOutputStream);
            oo.writeObject(object);
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            result = (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSIMCardReady(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSimpleTips(int code) {
        switch (code) {
            case Constants.Code.ERROR:
                return "失败";
            case Constants.Code.ERROR_AUTH_DENIED:
                return "认证失败";
            case Constants.Code.ERROR_CANCEL:
                return "取消";
            case Constants.Code.ERROR_LOGIN:
                return "登录失败";
            case Constants.Code.ERROR_NOT_INSTALL:
                return "应用程序未安装";
            case Constants.Code.ERROR_NOT_SUPPORT:
                return "不支持此功能";
            case Constants.Code.SUCCESS:
                return "成功";
            default:
                return "未知";
        }
    }

}
