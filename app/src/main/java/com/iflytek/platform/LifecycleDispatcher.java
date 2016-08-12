package com.iflytek.platform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/12,10:10
 * @see
 */
public final class LifecycleDispatcher {

    private LifecycleDispatcher() {
        throw new AssertionError();
    }

    public static void onCreate(Activity activity, Bundle bundle) {
        PlatformHelper.INSTANCE.lifecycleCallbacks.onCreate(activity, bundle);
    }

    public static void onNewIntent(Activity activity, Intent intent) {
        PlatformHelper.INSTANCE.lifecycleCallbacks.onNewIntent(activity, intent);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        PlatformHelper.INSTANCE.lifecycleCallbacks.onActivityResult(activity, requestCode, resultCode, data);
    }

}
