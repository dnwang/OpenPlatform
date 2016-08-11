package com.iflytek.platform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.callbacks.OnLoginListener;
import com.iflytek.platform.callbacks.OnShareListener;
import com.iflytek.platform.entity.ShareContent;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:21
 * @see
 */
abstract class SocialPlatform implements Platform, ActivityLifecycleCallbacks {

    public abstract void share(ShareContent content, OnShareListener listener);

    public abstract void login(OnLoginListener listener);

    @Override
    public void onCreate(Activity activity, Bundle bundle) {

    }

    @Override
    public void onStart(Activity activity) {

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
    public void onSaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

}
