package com.iflytek.platform.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.iflytek.platform.Channel;
import com.iflytek.platform.callbacks.Callback;
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
 * @version 8/11/16,22:55
 * @see
 */
final class WeixinCircle extends Channel implements Socialize {

    private Callback<Object> shareCallback;

    public WeixinCircle(Context context) {
        super(context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (WeixinAuthActivity.REQ_WEIXIN == requestCode && Activity.RESULT_OK == resultCode) {
            final int code = data.getIntExtra(Constants.KEY_CODE, -1);
            if (null != shareCallback) {
                shareCallback.call(null, null, code);
            }
        }
        shareCallback = null;
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        shareCallback = null;
        if (null == content) {
            return;
        }
        if (WeixinAuthActivity.startActivity((Activity) getContext(), WeixinAuthActivity.TYPE_SHARE_CIRCLE, content)) {
            shareCallback = callback;
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

}
