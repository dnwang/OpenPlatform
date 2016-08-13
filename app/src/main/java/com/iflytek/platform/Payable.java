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
 * @version 8/13/16,08:06
 * @see
 */
interface Payable {

    void pay(Context context, PayInfo payInfo, Callback callback);

}
