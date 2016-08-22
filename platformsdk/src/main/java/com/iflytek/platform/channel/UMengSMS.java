package com.iflytek.platform.channel;

import android.content.Context;

import com.iflytek.platform.Channel;
import com.iflytek.platform.callbacks.Callback;
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
final class UMengSMS extends Channel implements Socialize {

    private static final String APP_KEY = "5281d19656240bee4f069f1d";

    static {

    }

    public UMengSMS(Context context) {
        super(context);
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {

    }

    @Override
    public void login(Callback<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {

    }
}
