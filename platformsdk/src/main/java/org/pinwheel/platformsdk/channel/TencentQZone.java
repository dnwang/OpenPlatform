package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;

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
