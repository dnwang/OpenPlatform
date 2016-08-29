package com.iflytek.platform.entity;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:40
 * @see
 */
public class AccountInfo implements Serializable {

    public String id;
    public String nickName;
    public int gender; // 1:男;2女;0:未知
    public String headerImg;
    public AccessToken token;

    private Map<String, String> extra;

    public AccountInfo() {
        extra = new HashMap<>();
    }

    public void putExtra(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        extra.put(key, value);
    }

    public String getExtra(String key) {
        if (TextUtils.isEmpty(key) || !extra.containsKey(key)) {
            return "";
        }
        return extra.get(key);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}
