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
 * @version 2016/8/18,10:05
 * @see
 */
final class Taobao extends Channel implements Socialize {

    private Callback<AccountInfo> loginCallback;

    public Taobao(Context context) {
        super(context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (TaobaoAuthActivity.REQ_TAOBAO == requestCode && Activity.RESULT_OK == resultCode) {
            final int code = data.getIntExtra(Constants.KEY_CODE, -1);
            final Object obj = data.getSerializableExtra(Constants.KEY_CONTENT);
            if (null != loginCallback) {
                final AccountInfo accountInfo = (null != obj && obj instanceof AccountInfo) ? (AccountInfo) obj : null;
                loginCallback.call(accountInfo, null, code);
            }
        }
        loginCallback = null;
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
        loginCallback = callback;
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

}
