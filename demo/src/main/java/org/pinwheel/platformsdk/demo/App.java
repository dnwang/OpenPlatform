package org.pinwheel.platformsdk.demo;

import android.app.Application;

import org.pinwheel.platformsdk.PlatformConfig;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/23,16:38
 * @see
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化平台配置
        PlatformConfig.INSTANCE.init(getApplicationContext());
    }

}
