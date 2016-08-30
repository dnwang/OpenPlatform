package com.iflytek.platform.channel;

import android.content.Context;

import com.iflytek.platform.Channel;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.PayInfo;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:55
 * @see
 */
final class AliPay extends Channel implements Payable {

    public AliPay(Context context) {
        super(context);
    }

    @Override
    public void pay(PayInfo payInfo, Callback<Object> callback) {
        if (null != callback) {
            callback.call(ChannelType.ALIPAY, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }
}