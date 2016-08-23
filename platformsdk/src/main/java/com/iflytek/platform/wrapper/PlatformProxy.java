package com.iflytek.platform.wrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.PlatformBehavior;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccountInfo;

import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/22/16,21:38
 * @see
 */
public class PlatformProxy extends Activity {


    public static void share(ChannelType type, Callback<Object> callback) {

    }

    public static void login(ChannelType type, Callback<AccountInfo> callback) {

    }

    public static void getFriends(ChannelType type, Callback<List<AccountInfo>> callback) {

    }

    private PlatformBehavior platformBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        platformBehavior = new PlatformBehavior(this);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(0);
        platformBehavior.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        platformBehavior.onNewIntent(this, intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        platformBehavior.onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        platformBehavior.onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        platformBehavior.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        platformBehavior.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        platformBehavior.onStop(this);
    }

    @Override
    protected void onDestroy() {
        platformBehavior.clear();
        super.onDestroy();
        platformBehavior.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        platformBehavior.onActivityResult(this, requestCode, resultCode, data);
    }

}
