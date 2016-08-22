package com.iflytek.platform;

import com.iflytek.platform.entity.Constants;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/16,10:50
 * @see
 */
public enum ChannelType {

    SMS("UMengSMS", Constants.PlatformFlag.UMENG),
    WEIBO("SinaWeibo", Constants.PlatformFlag.SINA),
    QQ("TencentQQ", Constants.PlatformFlag.TENCENT),
    QZONE("TencentQZone", Constants.PlatformFlag.TENCENT),
    WEIXIN("Weixin", Constants.PlatformFlag.WEIXIN),
    WEIXIN_CIRCLE("WeixinCircle", Constants.PlatformFlag.WEIXIN),
    ALIPAY("AliPay", Constants.PlatformFlag.ALIPAY),
    TAOBAO("Taobao", Constants.PlatformFlag.TAOBAO);

    private String className;
    private int flag;

    /**
     * @param className 对应的实体类名
     * @param flag      所属平台标志
     */
    ChannelType(String className, int flag) {
        this.className = className;
        this.flag = flag;
    }

    String getClassName() {
        return className;
    }

    int getFlag() {
        return flag;
    }

}
