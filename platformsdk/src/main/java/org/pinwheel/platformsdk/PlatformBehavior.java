package org.pinwheel.platformsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.channel.ChannelType;
import org.pinwheel.platformsdk.channel.Payable;
import org.pinwheel.platformsdk.channel.SilentlySocialize;
import org.pinwheel.platformsdk.channel.Socialize;
import org.pinwheel.platformsdk.entity.AccessToken;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.PayInfo;
import org.pinwheel.platformsdk.entity.ShareContent;

import java.util.EnumMap;
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
public final class PlatformBehavior implements ActivityLifecycleCallbacks, Socialize, SilentlySocialize, Payable {

    private Context context;

    private Map<ChannelType, Channel> cache;
    private Pair<ChannelType, Channel> channelPair;

    public PlatformBehavior(Context context) {
        this.context = context;
        this.cache = new EnumMap<>(ChannelType.class);
    }

    public PlatformBehavior select(ChannelType type) {
        channelPair = null;
        if (null != type) {
            Channel channel = cache.get(type);
            if (null == channel) {
                channel = ChannelType.getEntity(context, type);
                cache.put(type, channel);
            }
            channelPair = new Pair<>(type, channel);
        }
        return this;
    }

    public PlatformBehavior clear() {
        channelPair = null;
        cache.clear();
        return this;
    }

    @Override
    public void pay(PayInfo payInfo, Callback<Object> callback) {
        if (null == channelPair) {
            return;
        }
        if (null == payInfo) {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR);
            }
            return;
        }
        if (channelPair.second instanceof Payable) {
            ((Payable) channelPair.second).pay(payInfo, callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void share(ShareContent content, Callback<Object> callback) {
        if (null == channelPair) {
            return;
        }
        if (null == content) {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR);
            }
            return;
        }
        if (channelPair.second instanceof Socialize) {
            ((Socialize) channelPair.second).share(content, callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        if (null == channelPair) {
            return;
        }
        if (channelPair.second instanceof Socialize) {
            ((Socialize) channelPair.second).login(callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null == channelPair) {
            return;
        }
        if (channelPair.second instanceof Socialize) {
            ((Socialize) channelPair.second).getFriends(callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void getFriends(AccessToken token, Callback<List<AccountInfo>> callback) {
        if (null == channelPair) {
            return;
        }
        if (null == token) {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_AUTH_DENIED);
            }
            return;
        }
        if (channelPair.second instanceof SilentlySocialize) {
            ((SilentlySocialize) channelPair.second).getFriends(token, callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void share(AccessToken token, ShareContent content, Callback<Object> callback) {
        if (null == channelPair) {
            return;
        }
        if (null == content) {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR);
            }
            return;
        }
        if (channelPair.second instanceof SilentlySocialize) {
            ((SilentlySocialize) channelPair.second).share(token, content, callback);
        } else {
            if (null != callback) {
                callback.call(channelPair.first, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        }
    }

    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onCreate(activity, bundle);
    }

    @Override
    public void onStart(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onStart(activity);
    }

    @Override
    public void onRestart(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onRestart(activity);
    }

    @Override
    public void onResume(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onResume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onPause(activity);
    }

    @Override
    public void onStop(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onStop(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onDestroy(activity);
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle bundle) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onSaveInstanceState(activity, bundle);
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onNewIntent(activity, intent);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (null == channelPair) {
            return;
        }
        channelPair.second.onActivityResult(activity, requestCode, resultCode, data);
    }

}
