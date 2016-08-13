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
public enum PlatformHelper implements ActivityLifecycleCallbacks {

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
        if (platform instanceof Payable) {
            ((Payable) platform).pay(context, payInfo, callback);
        }
    }

    public void share(Context context, Platform.Type type, ShareContent content, Callback callback) {
        Platform platform = getPlatform(context, type);
        if (platform instanceof Socialize) {
            ((Socialize) platform).share(context, content, callback);
        }
    }

    public void login(Context context, Platform.Type type, Callback2<AccountInfo> callback) {
        Platform platform = getPlatform(context, type);
        if (platform instanceof Socialize) {
            ((Socialize) platform).login(context, callback);
        }
    }

    public void getFriends(Context context, Platform.Type type, Callback2<List<AccountInfo>> Callback) {
        Platform platform = getPlatform(context, type);
        if (platform instanceof Socialize) {
            ((Socialize) platform).getFriends(context, Callback);
        }
    }


    // Activity lifecycle callback

    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onCreate(activity, bundle);
        }
    }

    @Override
    public void onStart(Activity activity) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onStart(activity);
        }
    }

    @Override
    public void onRestart(Activity activity) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onRestart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onResume(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onDestroy(activity);
        }
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle bundle) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onSaveInstanceState(activity, bundle);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onNewIntent(activity, intent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (null == platformMap) {
            return;
        }
        final Collection<Platform> platforms = platformMap.values();
        for (Platform platform : platforms) {
            platform.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

}
