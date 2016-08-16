package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

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
final class TencentQQ extends Platform implements Socialize {

    static final String APP_ID = "100526240";
    static final String APP_KEY = "20bca3e9e564042b7d1e2ec6ee261b1c";

    private Tencent shareApi;
    private Callback2<AccountInfo> loginCallback;

    public TencentQQ(Context context) {
        super(context);
        shareApi = Tencent.createInstance(APP_ID, context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
                @Override
                public void onComplete(Object obj) {
                    if (null != loginCallback) {
                        loginCallback.call(new AccountInfo(), true, null, 0);// TODO: 8/15/16  code
                    }

                    if (null == obj) {
                        Toast.makeText(getContext(), "返回为空,登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject json = (JSONObject) obj;
                    if (json.length() == 0) {
                        Toast.makeText(getContext(), "返回为空,登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(UiError e) {
                    if (null != loginCallback) {
                        loginCallback.call(null, false, e.errorMessage, StateCodes.ERROR);
                    }
                }

                @Override
                public void onCancel() {
                    if (null != loginCallback) {
                        loginCallback.call(null, false, null, StateCodes.ERROR_CANCEL);
                    }
                }
            });
            loginCallback = null;
        }
    }

    @Override
    public void share(ShareContent content, final Callback callback) {
        if (null == content || TextUtils.isEmpty(content.targetUrl)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, content.targetUrl);//必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content.content);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, content.imageUrl);

        shareApi.shareToQQ((Activity) getContext(), params, new IUiListener() {
            @Override
            public void onCancel() {
                if (null != callback) {
                    callback.call(false, null, StateCodes.ERROR_CANCEL);
                }
            }

            @Override
            public void onComplete(Object response) {
                if (null != callback) {
                    callback.call(true, null, StateCodes.SUCCESS);
                }
            }

            @Override
            public void onError(UiError e) {
                if (null != callback) {
                    callback.call(false, e.errorMessage, StateCodes.ERROR);
                }
            }
        });
    }

    @Override
    public void login(Callback2<AccountInfo> callback) {
        loginCallback = callback;
        // TODO: 2016/8/16
    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, false, null, StateCodes.ERROR_NOT_SUPPORT);
        }
    }
}
