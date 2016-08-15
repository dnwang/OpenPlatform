package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

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
final class TencentQZone extends Platform implements Socialize {

    private static final String APP_ID = TencentQQ.APP_ID;
    private static final String APP_KEY = TencentQQ.APP_KEY;

    private Tencent shareApi;

    public TencentQZone(Context context) {
        super(context);
        shareApi = Tencent.createInstance(APP_ID, context);
    }

    @Override
    public void share(Context context, ShareContent content, final Callback callback) {
        if (!(context instanceof Activity)) {
            return;
        }
        if (null == content || TextUtils.isEmpty(content.targetUrl)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, content.targetUrl);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content.content);
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, content.imageUrl);

        shareApi.shareToQzone((Activity) context, params, new IUiListener() {
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
    public void login(Context context, Callback2<AccountInfo> callback) {
        if (null != callback) {
            callback.call(null, false, null, StateCodes.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(Context context, Callback2<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, false, null, StateCodes.ERROR_NOT_SUPPORT);
        }
    }

}
