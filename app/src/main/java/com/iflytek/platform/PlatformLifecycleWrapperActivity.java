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
 * @version 8/14/16,23:22
 * @see
 */
public abstract class PlatformLifecycleWrapperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFilter) {
            PlatformHelper.onCreate(this, savedInstanceState);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isFilter) {
            PlatformHelper.onNewIntent(this, intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isFilter) {
            PlatformHelper.onStart(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFilter) {
            PlatformHelper.onRestart(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFilter) {
            PlatformHelper.onResume(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFilter) {
            PlatformHelper.onStop(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFilter) {
            PlatformHelper.onDestroy(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFilter) {
            PlatformHelper.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private boolean isFilter = false;

    protected final void requestPlatformLifecycle(boolean is) {
        this.isFilter = is;
    }

}
