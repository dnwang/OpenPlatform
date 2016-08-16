package com.iflytek.platform.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;

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

}
