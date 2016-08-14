package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    private Map<Platform.Type, Platform> platformMap = null;

    private Platform getPlatform(Context context, Platform.Type type) {
        Platform platform = null;
        if (null == platformMap) {
            platformMap = new HashMap<>();
        }
        if (!platformMap.isEmpty() && platformMap.containsKey(type)) {
            platform = platformMap.get(type);
        } else {
            try {
                platform = type.clazz.getConstructor(Context.class).newInstance(context);
                platformMap.put(type, platform);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return platform;
    }

    public void pay(Context context, Platform.Type type, PayInfo payInfo, Callback callback) {
        Platform platform = getPlatform(context, type);
        if (null != platform && platform instanceof Payable) {
            ((Payable) platform).pay(context, payInfo, callback);
        }
    }

    public void share(Context context, Platform.Type type, ShareContent content, Callback callback) {
        Platform platform = getPlatform(context, type);
        if (null != platform && platform instanceof Socialize) {
            ((Socialize) platform).share(context, content, callback);
        }
    }

    public void login(Context context, Platform.Type type, Callback2<AccountInfo> callback) {
        Platform platform = getPlatform(context, type);
        if (null != platform && platform instanceof Socialize) {
            ((Socialize) platform).login(context, callback);
        }
    }

    public void getFriends(Context context, Platform.Type type, Callback2<List<AccountInfo>> Callback) {
        Platform platform = getPlatform(context, type);
        if (null != platform && platform instanceof Socialize) {
            ((Socialize) platform).getFriends(context, Callback);
        }
    }


    // Activity lifecycle callback
    static void onCreate(Activity activity, Bundle bundle) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onCreate(activity, bundle);
        }
    }

    static void onStart(Activity activity) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onStart(activity);
        }
    }

    static void onRestart(Activity activity) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onRestart(activity);
        }
    }

    static void onResume(Activity activity) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onResume(activity);
        }
    }

    static void onStop(Activity activity) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onStop(activity);
        }
    }

    static void onDestroy(Activity activity) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onDestroy(activity);
        }
    }

    static void onSaveInstanceState(Activity activity, Bundle bundle) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onSaveInstanceState(activity, bundle);
        }
    }

    static void onNewIntent(Activity activity, Intent intent) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onNewIntent(activity, intent);
        }
    }

    static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (null == INSTANCE.platformMap) {
            return;
        }
        final Collection<Platform> platforms = INSTANCE.platformMap.values();
        for (Platform platform : platforms) {
            platform.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

}
