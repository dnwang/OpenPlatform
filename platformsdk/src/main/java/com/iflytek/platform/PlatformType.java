package com.iflytek.platform;

import android.content.Context;

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
public enum PlatformType {

    SMS(UMengSMS.class),
    WEIBO(SinaWeibo.class),
    QQ(TencentQQ.class),
    QZONE(TencentQZone.class),
    WEIXIN(Weixin.class),
    WEIXIN_CIRCLE(WeixinCircle.class),
    ALIPAY(AliPay.class);

    private Class<? extends Platform> cls;

    PlatformType(Class<? extends Platform> cls) {
        this.cls = cls;
    }

    public Platform getInstance(Context context) {
        try {
            return cls.getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
