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
 * @version 8/11/16,22:13
 * @see
 */
interface ActivityLifecycleCallbacks {

    void onCreate(Activity activity, Bundle bundle);

    void onStart(Activity activity);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void onStop(Activity activity);

    void onSaveInstanceState(Activity activity, Bundle bundle);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    void onDestroy(Activity activity);

}
