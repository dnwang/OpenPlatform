package com.iflytek.platform;

import android.content.Context;

import com.iflytek.platform.callbacks.Callback;
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
final class AliPay extends Platform implements Payable {

    public AliPay(Context context) {
        super(context);
    }

    @Override
    public void pay(Context context, PayInfo payInfo, Callback callback) {

    }
}
