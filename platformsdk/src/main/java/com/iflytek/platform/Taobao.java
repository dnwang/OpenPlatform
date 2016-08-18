package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.TaobaoAuthActivity;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.ShareContent;

import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/18,10:05
 * @see
 */
public class Taobao extends Platform implements Socialize {

    public Taobao(Context context) {
        super(context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // TODO: 2016/8/18
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        TaobaoAuthActivity.startActivity((Activity) getContext());
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

}
