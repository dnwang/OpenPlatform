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
 * @version 2016/8/18,10:05
 * @see
 */
final class Taobao extends Channel implements Socialize {

    private Callback<AccountInfo> loginTempCallback;

    public Taobao(Context context) {
        super(context);
    }

    private final IntentFilter filter = new IntentFilter(TaobaoAuthActivity.ACTION_TAOBAO_RESULT);

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unRegisterReceiver();
            final String action = intent.getAction();
            if (TaobaoAuthActivity.ACTION_TAOBAO_RESULT.equals(action)) {
                final int code = intent.getIntExtra(Constants.KEY_CODE, Constants.Code.ERROR);
                final Object obj = intent.getSerializableExtra(Constants.KEY_CONTENT);
                final AccountInfo accountInfo = (null != obj && obj instanceof AccountInfo) ? (AccountInfo) obj : null;
                dispatchCallback(loginTempCallback, accountInfo, null, code);
            }
            loginTempCallback = null;
        }
    };

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        TaobaoAuthActivity.startActivity(getContext());
        registerReceiver();
        loginTempCallback = callback;
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    private <T> void dispatchCallback(Callback<T> callback, T obj, String msg, int code) {
        if (null != callback) {
            callback.call(ChannelType.TAOBAO, obj, msg, code);
        }
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

}
