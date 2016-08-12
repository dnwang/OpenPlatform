package com.iflytek.platform;

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

    void onCreate(Bundle bundle);

    void onStart();

    void onRestart();

    void onResume();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle bundle);

    void onNewIntent(Intent intent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
