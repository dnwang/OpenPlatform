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
public final class PlatformHelper implements ActivityLifecycleCallbacks, Socialize, Payable {

    private Context context;

    private Map<PlatformType, Platform> cache;
    private Map<PlatformType, Platform> selected;

    public PlatformHelper(Context context) {
        this.context = context;
        this.cache = new HashMap<>();
        this.selected = new HashMap<>();
    }

    public PlatformHelper select(PlatformType... types) {
        selected.clear();
        if (null != types && types.length > 0) {
            for (PlatformType type : types) {
                if (null == type) {
                    continue;
                }
                Platform platform = cache.get(type);
                if (null == platform) {
                    platform = type.getInstance(context);
                    cache.put(type, platform);
                }
                selected.put(type, platform);
            }
        }
        return this;
    }

    public PlatformHelper clear() {
        selected.clear();
        cache.clear();
        return this;
    }

    @Override
    public void pay(PayInfo payInfo, Callback callback) {
        for (Platform platform : selected.values()) {
            if (platform instanceof Payable) {
                ((Payable) platform).pay(payInfo, callback);
            }
        }
    }

    @Override
    public void share(ShareContent content, Callback callback) {
        for (Platform platform : selected.values()) {
            if (platform instanceof Socialize) {
                ((Socialize) platform).share(content, callback);
            }
        }
    }

    @Override
    public void login(Callback2<AccountInfo> callback) {
        for (Platform platform : selected.values()) {
            if (platform instanceof Socialize) {
                ((Socialize) platform).login(callback);
            }
        }
    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {
        for (Platform platform : selected.values()) {
            if (platform instanceof Socialize) {
                ((Socialize) platform).getFriends(callback);
            }
        }
    }


    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        for (Platform platform : selected.values()) {
            platform.onCreate(activity, bundle);
        }
    }

    @Override
    public void onStart(Activity activity) {
        for (Platform platform : selected.values()) {
            platform.onStart(activity);
        }
    }

    @Override
    public void onRestart(Activity activity) {
        for (Platform platform : selected.values()) {
            platform.onRestart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        for (Platform platform : selected.values()) {
            platform.onResume(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        for (Platform platform : selected.values()) {
            platform.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        for (Platform platform : selected.values()) {
            platform.onDestroy(activity);
        }
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle bundle) {
        for (Platform platform : selected.values()) {
            platform.onSaveInstanceState(activity, bundle);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        for (Platform platform : selected.values()) {
            platform.onNewIntent(activity, intent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        for (Platform platform : selected.values()) {
            platform.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

}
