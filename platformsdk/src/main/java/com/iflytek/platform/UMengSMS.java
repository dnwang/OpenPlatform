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
 * @version 2016/8/17,11:03
 * @see
 */
final class UMengSMS extends Platform implements Socialize {

    private static final String APP_KEY = "5281d19656240bee4f069f1d";

    static {

    }

    public UMengSMS(Context context) {
        super(context);
    }

    @Override
    public void share(ShareContent content, Callback callback) {

    }

    @Override
    public void login(Callback2<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {

    }
}
