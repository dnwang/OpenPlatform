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
    public boolean gender; // true:男
    public String headerImg;
    public String token;
    public int expireDate;

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

}
