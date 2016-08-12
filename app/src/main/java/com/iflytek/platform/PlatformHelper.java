package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.Map;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,20:30
 * @see
 */
public enum PlatformHelper {

    INSTANCE;

    final ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {

        private Context context;

        @Override
        public void onCreate(Activity activity, Bundle bundle) {

        }

        @Override
        public void onNewIntent(Activity activity, Intent intent) {

        }

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        }
    };

    private Platform handlePlatform;

    private Map<PlatformType, Platform> platforms;

    public void initialize(Context context) {
        


    }

    public void share(Context context, PlatformType platformType, ShareContent content, Socialize.OnShareListener listener) {
        Platform platform = platforms.get(platformType);

        if (null != platform && platform instanceof Socialize) {
            ((Socialize)platform).share(content, listener);
            handlePlatform = platform;
        }
    }

    public void login(Context context, PlatformType platformType, Socialize.OnLoginListener listener) {

    }

    public void pay(Context context, PlatformType platformType, PayInfo payInfo, Payable.OnPayListener listener) {

    }

}
