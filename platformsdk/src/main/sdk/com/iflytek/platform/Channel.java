package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
public abstract class Channel implements ActivityLifecycleCallbacks {

    private Context context;

    public Channel(Context context) {
        this.context = context;
    }

    protected final Context getContext() {
        return context;
    }

    @Override
    public void onCreate(Activity activity, Bundle bundle) {

    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onRestart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

}
