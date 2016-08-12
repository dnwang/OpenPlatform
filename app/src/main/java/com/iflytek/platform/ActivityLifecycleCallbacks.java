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

    void onNewIntent(Activity activity, Intent intent);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

}
