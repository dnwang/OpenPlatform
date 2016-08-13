package com.iflytek.platform;

import android.content.Context;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
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
final class WeixinCircle extends Platform implements Socialize {

    private static final String APP_ID = "wxf04bacbcee9b5cc7";
    private static final String APP_SECRET = "9299bfd1ec0104a4cad2faa23010a580";

    public WeixinCircle(Context context) {
        super(context);
    }

    @Override
    public void share(Context context, ShareContent content, Callback callback) {

    }

    @Override
    public void login(Context context, Callback2<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Context context, Callback2<List<AccountInfo>> Callback) {

    }
}
