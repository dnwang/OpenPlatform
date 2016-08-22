package com.iflytek.platform;

import android.text.TextUtils;

import com.iflytek.platform.entity.Constants;

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

    private String weixinId;
    private String weixinSecret;

    private String tencentId;
    private String tencentKey;

    private String sinaKey;
    private String sinaSecret;

    private String taobaoKey;
    private String taobaoSecret;

    private int initializedPlatforms;

    PlatformConfig() {

    }

    public boolean isInitialized(ChannelType type) {
        return (initializedPlatforms & type.getFlag()) == type.getFlag();
    }

    public void setSina(String key, String secret) {
        sinaKey = key;
        sinaSecret = secret;
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(secret)) {
            initializedPlatforms |= Constants.PlatformFlag.SINA;
        } else {
            initializedPlatforms &= ~Constants.PlatformFlag.SINA;
        }
    }

    public void setWeixin(String id, String secret) {
        weixinId = id;
        weixinSecret = secret;
        if (TextUtils.isEmpty(id) && TextUtils.isEmpty(secret)) {
            initializedPlatforms |= Constants.PlatformFlag.WEIXIN;
        } else {
            initializedPlatforms &= ~Constants.PlatformFlag.WEIXIN;
        }
    }

    public void setTencent(String id, String key) {
        tencentId = id;
        tencentKey = key;
        if (TextUtils.isEmpty(id) && TextUtils.isEmpty(key)) {
            initializedPlatforms |= Constants.PlatformFlag.TENCENT;
        } else {
            initializedPlatforms &= ~Constants.PlatformFlag.TENCENT;
        }
    }

    public void setTaobao(String key, String secret) {
        taobaoKey = key;
        taobaoSecret = secret;
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(key)) {
            initializedPlatforms |= Constants.PlatformFlag.TAOBAO;
        } else {
            initializedPlatforms &= ~Constants.PlatformFlag.TAOBAO;
        }
    }

    public String getWeixinId() {
        return weixinId;
    }

    public String getWeixinSecret() {
        return weixinSecret;
    }

    public String getTencentId() {
        return tencentId;
    }

    public String getTencentKey() {
        return tencentKey;
    }

    public String getSinaKey() {
        return sinaKey;
    }

    public String getSinaSecret() {
        return sinaSecret;
    }

    public String getTaobaoKey() {
        return taobaoKey;
    }

    public String getTaobaoSecret() {
        return taobaoSecret;
    }

}
