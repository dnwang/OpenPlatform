package com.iflytek.platform;

import com.iflytek.platform.entity.PayInfo;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:21
 * @see
 */
public interface Payable {

    interface OnPayListener {
        void onComplete(boolean isSuccess, String msg, int code);
    }

    void pay(PayInfo payInfo, OnPayListener listener);

}
