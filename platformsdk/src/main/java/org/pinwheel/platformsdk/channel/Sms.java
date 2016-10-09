package org.pinwheel.platformsdk.channel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;

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
final class Sms extends Channel implements Socialize {

    public Sms(Context context) {
        super(context);
    }

    @Override
    public void share(ShareContent content, final Callback<Object> callback) {
        if (null == content) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR);
            return;
        }
        if (!Tools.isSIMCardReady(getContext())) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_INSTALL);
            return;
        }
        final String smsText = ContentConverter.getSimpleContent(content);
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body", smsText);
        getContext().startActivity(intent);
        dispatchCallback(callback, null, null, Constants.Code.SUCCESS);
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    private <T> void dispatchCallback(Callback<T> callback, T obj, String msg, int code) {
        if (null != callback) {
            callback.call(ChannelType.SMS, obj, msg, code);
        }
    }

}
