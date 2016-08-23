package com.iflytek.platform.channel;

import android.content.Context;

import com.iflytek.platform.Channel;

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

    SMS(UMengSMS.class),
    WEIBO(SinaWeibo.class),
    QQ(TencentQQ.class),
    QZONE(TencentQZone.class),
    WEIXIN(Weixin.class),
    WEIXIN_CIRCLE(WeixinCircle.class),
    ALIPAY(AliPay.class),
    TAOBAO(Taobao.class);

    public static Channel getEntity(Context context, ChannelType type) {
        try {
            return type.cls.getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<? extends Channel> cls;

    ChannelType(Class<? extends Channel> cls) {
        this.cls = cls;
    }

}
