package org.pinwheel.platformsdk.channel;

import android.content.Context;

import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.PayInfo;

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
