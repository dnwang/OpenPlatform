package com.iflytek.platform.entity;

import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.connect.auth.QQToken;

import java.io.Serializable;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/29,14:41
 * @see
 */
public final class AccessToken implements Serializable {

    public static AccessToken createToken(String uid, String token) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
            return null;
        }
        return new AccessToken(uid, token);
    }

    public static AccessToken createToken(Oauth2AccessToken oauth2AccessToken) {
        if (null == oauth2AccessToken) {
            return null;
        }
        final String uid = oauth2AccessToken.getUid();
        final String token = oauth2AccessToken.getToken();
        return createToken(uid, token);
    }

    public static AccessToken createToken(QQToken qqToken) {
        if (null == qqToken) {
            return null;
        }
        final String uid = qqToken.getOpenId();
        final String token = qqToken.getAccessToken();
        return createToken(uid, token);
    }

    private String uid;
    private String token;

    private AccessToken(String uid, String token) {
        this.uid = uid;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

}
