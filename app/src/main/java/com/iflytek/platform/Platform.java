package com.iflytek.platform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,20:52
 * @see
 */
public abstract class Platform implements ActivityLifecycleCallbacks {

    public enum Type {

        WEIBO(SinaWeibo.class),
        ALIPAY(AliPay.class);

        Class<? extends Platform> clazz;

        Type(Class<? extends Platform> clazz) {
            this.clazz = clazz;
        }
    }

    public interface Callback {
        void call(boolean isSuccess, String msg, int code);
    }

    public interface Callback2<T> {
        void call(T t, boolean isSuccess, String msg, int code);
    }

    public abstract void pay(Context context, PayInfo payInfo, Callback callback);

    public abstract void share(Context context, ShareContent content, Callback callback);

    public abstract void login(Context context, Callback2<AccountInfo> callback);

    public abstract void getFriends(Context context, Callback2<List<AccountInfo>> Callback);

    public Platform(Context context) {

    }


    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
