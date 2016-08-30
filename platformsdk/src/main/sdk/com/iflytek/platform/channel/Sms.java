package com.iflytek.platform.channel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.iflytek.platform.Channel;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccessToken;
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
            return;
        }
        final String smsText = content.title + "\n" + content.content + "\n" + content.targetUrl;
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", smsText);
        getContext().startActivity(intent);
        if (null != callback) {
            callback.call(ChannelType.SMS, null, null, Constants.Code.SUCCESS);
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        if (null != callback) {
            callback.call(ChannelType.SMS, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.SMS, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void getFriends(AccessToken token, Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.SMS, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }
}
