package com.iflytek.platform.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.iflytek.platform.Channel;
import com.iflytek.platform.PlatformConfig;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.ShareContent;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

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
        if (null == content) {
            if (null != callback) {
                callback.call(ChannelType.QZONE, null, null, Constants.Code.ERROR);
            }
            return;
        }
        shareCallback = new TencentQQ.UIListenerWrapper<>(ChannelType.QZONE, callback);
        shareApi.shareToQzone((Activity) getContext(), ContentConverter.getQZoneContent(content), shareCallback);
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

}
