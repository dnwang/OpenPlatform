package org.pinwheel.platformsdk.channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unRegisterReceiver();
            final String action = intent.getAction();
            if (WeixinAuthActivity.ACTION_WEIXIN_RESULT.equals(action)) {
                final int code = intent.getIntExtra(Constants.KEY_CODE, Constants.Code.ERROR);
                final Object obj = intent.getSerializableExtra(Constants.KEY_CONTENT);
                // share
                dispatchCallback(shareCallback, null, null, code);
                // login
                final AccountInfo accountInfo = (null != obj && obj instanceof AccountInfo) ? (AccountInfo) obj : null;
                dispatchCallback(loginCallback, accountInfo, null, code);
            }
            shareCallback = null;
            loginCallback = null;
        }
    };

    public Weixin(Context context) {
        super(context);
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        shareCallback = null;
        if (!isWeiXinAppInstalled(getContext())) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_INSTALL);
            return;
        }
        if (null == content) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR);
            return;
        }
        if (WeixinAuthActivity.startActivity(getContext(), WeixinAuthActivity.TYPE_SHARE_FRIEND, content)) {
            shareCallback = callback;
            registerReceiver();
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        loginCallback = null;
        if (!isWeiXinAppInstalled(getContext())) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_INSTALL);
            return;
        }
        if (WeixinAuthActivity.startActivity(getContext(), WeixinAuthActivity.TYPE_LOGIN, null)) {
            loginCallback = callback;
            registerReceiver();
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
    }

    static boolean isWeiXinAppInstalled(Context context) {
        final String qqPackageName = "com.tencent.mm";
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(qqPackageName, PackageManager.GET_ACTIVITIES);
            installed = null != packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
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
            callback.call(ChannelType.WEIXIN, obj, msg, code);
        }
    }

}
