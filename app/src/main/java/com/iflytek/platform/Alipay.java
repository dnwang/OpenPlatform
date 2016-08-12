package com.iflytek.platform;

import android.content.Context;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.List;

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
final class AliPay extends Platform {

    public AliPay(Context context) {
        super(context);
    }

    @Override
    public void pay(Context context, PayInfo payInfo, Callback callback) {

    }

    @Override
    public void share(Context context, ShareContent content, Callback callback) {

    }

    @Override
    public void login(Context context, Callback2<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Context context, Callback2<List<AccountInfo>> callback) {

    }
}
