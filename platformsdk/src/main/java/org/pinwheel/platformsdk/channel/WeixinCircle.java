package org.pinwheel.platformsdk.channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.pinwheel.platformsdk.Channel;
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
final class WeixinCircle extends Channel implements Socialize {

    private Callback<Object> shareCallback;

    private final IntentFilter filter = new IntentFilter(WeixinAuthActivity.ACTION_WEIXIN_RESULT);

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unRegisterReceiver();
            final String action = intent.getAction();
            if (WeixinAuthActivity.ACTION_WEIXIN_RESULT.equals(action)) {
                onCallback(intent);
            }
            shareCallback = null;
        }
    };

    public WeixinCircle(Context context) {
        super(context);
    }

    private void onCallback(Intent data) {
        final int code = (null == data) ? -1 : data.getIntExtra(Constants.KEY_CODE, -1);
        dispatchCallback(shareCallback, null, null, code);
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        shareCallback = null;
        if (null == content) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR);
            return;
        }
        if (WeixinAuthActivity.startActivity(getContext(), WeixinAuthActivity.TYPE_SHARE_CIRCLE, content)) {
            shareCallback = callback;
            registerReceiver();
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    private void registerReceiver() {
        if (null != getContext()) {
            getContext().registerReceiver(receiver, filter);
        }
    }

    private void unRegisterReceiver() {
        if (null != getContext()) {
            getContext().unregisterReceiver(receiver);
        }
    }

    private <T> void dispatchCallback(Callback<T> callback, T obj, String msg, int code) {
        if (null != callback) {
            callback.call(ChannelType.WEIXIN_CIRCLE, obj, msg, code);
        }
    }

}
