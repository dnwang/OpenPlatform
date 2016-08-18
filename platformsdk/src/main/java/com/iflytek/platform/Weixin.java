package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.iflytek.platform.callbacks.AbsWeixinApiActivity;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.Constants;

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
final class Weixin extends Platform implements Socialize {

    private Callback<Object> shareCallback;
    private Callback<AccountInfo> loginCallback;

    public Weixin(Context context) {
        super(context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (AbsWeixinApiActivity.REQ_WEIXIN == requestCode && Activity.RESULT_OK == resultCode) {
            final int code = data.getIntExtra(AbsWeixinApiActivity.FLAG_CODE, -1);
            final Object obj = data.getSerializableExtra(AbsWeixinApiActivity.FLAG_CONTENT);
            if (null != shareCallback) {
                shareCallback.call(null, null, code);
            }
            if (null != loginCallback) {
                final AccountInfo accountInfo = (null != obj && obj instanceof AccountInfo) ? (AccountInfo) obj : null;
                loginCallback.call(accountInfo, null, code);
            }
        }
        shareCallback = null;
        loginCallback = null;
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        shareCallback = null;
        if (null == content) {
            return;
        }
        if (AbsWeixinApiActivity.startActivity((Activity) getContext(), AbsWeixinApiActivity.TYPE_SHARE_FRIEND, content)) {
            shareCallback = callback;
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        loginCallback = null;
        if (AbsWeixinApiActivity.startActivity((Activity) getContext(), AbsWeixinApiActivity.TYPE_LOGIN, null)) {
            loginCallback = callback;
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

}
