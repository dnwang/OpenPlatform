package com.iflytek.platform.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.platform.Channel;
import com.iflytek.platform.PlatformConfig;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccessToken;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.ShareContent;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
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
final class TencentQZone extends Channel implements Socialize {

    private Tencent shareApi;
    private IUiListener shareCallback;

    public TencentQZone(Context context) {
        super(context);
        shareApi = Tencent.createInstance(PlatformConfig.INSTANCE.getTencentId(), context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE) {
            if (null != shareCallback) {
                Tencent.onActivityResultData(requestCode, resultCode, data, shareCallback);
            }
            shareCallback = null;
        }
    }

    @Override
    public void share(ShareContent content, final Callback<Object> callback) {
        if (null == content || TextUtils.isEmpty(content.targetUrl)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, content.targetUrl);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content.content);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>());

        shareCallback = new TencentQQ.UIListenerWrapper<>(ChannelType.QZONE, callback);
        shareApi.shareToQzone((Activity) getContext(), params, shareCallback);
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        if (null != callback) {
            callback.call(ChannelType.QZONE, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.QZONE, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(AccessToken token, Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.QZONE, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }
}
