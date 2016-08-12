package com.iflytek.platform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Collection;
import java.util.HashMap;
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
public enum PlatformHelper implements ActivityLifecycleCallbacks {

    INSTANCE;

    Map<Platform.Type, Platform> platformMap;

    PlatformHelper() {
        platformMap = new HashMap<>();
    }

    public void initialize(Context context) {
        for (Platform.Type type : Platform.Type.values()) {
            try {
                Platform platform = type.clazz.getConstructor(Context.class).newInstance(context);
                platformMap.put(type, platform);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Platform getPlatform(Platform.Type type) {
        return platformMap.get(type);
    }


    @Override
    public void onCreate(Bundle bundle) {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onCreate(bundle);
        }
    }

    @Override
    public void onStart() {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onStart();
        }
    }

    @Override
    public void onRestart() {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onRestart();
        }
    }

    @Override
    public void onResume() {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onResume();
        }
    }

    @Override
    public void onStop() {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onStop();
        }
    }

    @Override
    public void onDestroy() {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onSaveInstanceState(bundle);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onNewIntent(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onActivityResult(requestCode, resultCode, data);
        }
    }

}
