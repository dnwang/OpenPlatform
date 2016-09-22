package org.pinwheel.platformsdk.channel;

import android.app.Activity;
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
final class Weixin extends Channel implements Socialize {

    private Callback<Object> shareCallback;
    private Callback<AccountInfo> loginCallback;

    private final IntentFilter filter = new IntentFilter(WeixinAuthActivity.ACTION_WEIXIN_RESULT);
    /**
     * 代替{@link Activity#onActivityResult 接收数据回调}
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != getContext()) {
                getContext().unregisterReceiver(receiver);
            }
            final String action = intent.getAction();
            if (WeixinAuthActivity.ACTION_WEIXIN_RESULT.equals(action)) {
                onCallback(intent);
            }
            shareCallback = null;
            loginCallback = null;
        }
    };

    public Weixin(Context context) {
        super(context);
    }

    private void onCallback(Intent data) {
        final int code = (null == data) ? -1 : data.getIntExtra(Constants.KEY_CODE, -1);
        final Object obj = (null == data) ? null : data.getSerializableExtra(Constants.KEY_CONTENT);
        if (null != shareCallback) {
            shareCallback.call(ChannelType.WEIXIN, null, null, code);
        }
        if (null != loginCallback) {
            final AccountInfo accountInfo = (null != obj && obj instanceof AccountInfo) ? (AccountInfo) obj : null;
            loginCallback.call(ChannelType.WEIXIN, accountInfo, null, code);
        }
    }

    /**
     * 红米手机生命周期回调异常，在下次启动Activity才出发onActivityResult，导致不能正常的回调上层
     * 改为广播{@link Weixin#receiver}
     */
    @Deprecated
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (WeixinAuthActivity.REQ_WEIXIN == requestCode && Activity.RESULT_OK == resultCode) {
            onCallback(data);
        }
        shareCallback = null;
        loginCallback = null;
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        shareCallback = null;
        if (null == content) {
            return;
        }
        if (WeixinAuthActivity.startActivity((Activity) getContext(), WeixinAuthActivity.TYPE_SHARE_FRIEND, content)) {
            shareCallback = callback;
            registerReceiver();
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        loginCallback = null;
        if (WeixinAuthActivity.startActivity((Activity) getContext(), WeixinAuthActivity.TYPE_LOGIN, null)) {
            loginCallback = callback;
            registerReceiver();
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.WEIXIN, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    private void registerReceiver() {
        if (null != getContext()) {
            getContext().registerReceiver(receiver, filter);
        }
    }

}
