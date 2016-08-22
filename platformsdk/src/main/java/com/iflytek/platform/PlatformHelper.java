package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
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
    private Platform selected;

    public PlatformHelper(Context context) {
        this.context = context;
        this.cache = new HashMap<>();
    }

    public PlatformHelper select(PlatformType type) {
        selected = null;
        if (null != type) {
            Platform platform = cache.get(type);
            if (null == platform) {
                platform = type.getInstance(context);
                cache.put(type, platform);
            }
            selected = platform;
        }
        return this;
    }

    public PlatformHelper clear() {
        selected = null;
        cache.clear();
        return this;
    }

    @Override
    public void pay(PayInfo payInfo, Callback<Object> callback) {
        if (null != selected) {
            if (selected instanceof Payable) {
                ((Payable) selected).pay(payInfo, callback);
            } else {
                if (null != callback) {
                    callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
                }
            }
        }
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        if (null != selected) {
            if (selected instanceof Socialize) {
                ((Socialize) selected).share(content, callback);
            } else {
                if (null != callback) {
                    callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
                }
            }
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        if (null != selected) {
            if (selected instanceof Socialize) {
                ((Socialize) selected).login(callback);
            } else {
                if (null != callback) {
                    callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
                }
            }
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != selected) {
            if (selected instanceof Socialize) {
                ((Socialize) selected).getFriends(callback);
            } else {
                if (null != callback) {
                    callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
                }
            }
        }
    }


    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        if (null != selected) {
            selected.onCreate(activity, bundle);
        }
    }

    @Override
    public void onStart(Activity activity) {
        if (null != selected) {
            selected.onStart(activity);
        }
    }

    @Override
    public void onRestart(Activity activity) {
        if (null != selected) {
            selected.onRestart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        if (null != selected) {
            selected.onResume(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        if (null != selected) {
            selected.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        if (null != selected) {
            selected.onDestroy(activity);
        }
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle bundle) {
        if (null != selected) {
            selected.onSaveInstanceState(activity, bundle);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (null != selected) {
            selected.onNewIntent(activity, intent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (null != selected) {
            selected.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

}
