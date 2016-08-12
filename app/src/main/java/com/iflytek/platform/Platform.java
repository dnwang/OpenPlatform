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
abstract class Platform implements ActivityLifecycleCallbacks {

    abstract void initialize(Context context);

    @Override
    public void onCreate(Activity activity, Bundle bundle) {

    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

}
