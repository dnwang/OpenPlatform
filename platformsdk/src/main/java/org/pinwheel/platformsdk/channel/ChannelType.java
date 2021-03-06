package org.pinwheel.platformsdk.channel;

import android.content.Context;
import android.text.TextUtils;

import org.pinwheel.platformsdk.Channel;

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

    SMS(Sms.class),
    WEIBO(SinaWeibo.class),
    QQ(TencentQQ.class),
    QZONE(TencentQZone.class),
    WEIXIN(Weixin.class),
    WEIXIN_CIRCLE(WeixinCircle.class),
    ALIPAY(AliPay.class),
    TAOBAO(Taobao.class);

    public static ChannelType convert(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        for (ChannelType type : ChannelType.values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

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
