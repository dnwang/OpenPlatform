package com.iflytek.platform.wrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iflytek.platform.PlatformBehavior;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.utils.Tools;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/22/16,21:38
 * @see
 */
public final class PlatformProxy extends Activity {

    private static final String FLAG_BEHAVIOR = "behavior";
    private static final String FLAG_CHANNEL = "channel";
    private static final String FLAG_CONTENT = "content";
    private static final String FLAG_CALLBACK = "callback";

    private static final int BEHAVIOR_SHARE = 0x01;
    private static final int BEHAVIOR_LOGIN = 0x02;
    private static final int BEHAVIOR_GET_FRIENDS = 0x03;
    private static final int BEHAVIOR_PAY = 0x04;

    private static Callback callback;

    public static void share(Context context, ChannelType type, ShareContent content, Callback<Object> callback) {
        if (null == type) {
            return;
        }
        PlatformProxy.callback = callback;
        startActivity(context, BEHAVIOR_SHARE, type, content, getGlobCallbackHash());
    }

    public static void login(Context context, ChannelType type, Callback<AccountInfo> callback) {
        if (null == type) {
            return;
        }
        PlatformProxy.callback = callback;
        startActivity(context, BEHAVIOR_LOGIN, type, null, getGlobCallbackHash());
    }

    public static void getFriends(Context context, ChannelType type, Callback<List<AccountInfo>> callback) {
        if (null == type) {
            return;
        }
        PlatformProxy.callback = callback;
        startActivity(context, BEHAVIOR_GET_FRIENDS, type, null, getGlobCallbackHash());
    }

    public static void pay(Context context, ChannelType type, PayInfo payInfo, Callback<Object> callback) {
        if (null == type) {
            return;
        }
        PlatformProxy.callback = callback;
        startActivity(context, BEHAVIOR_PAY, type, payInfo, getGlobCallbackHash());
    }

    private static void startActivity(Context context, int behavior, ChannelType type, Serializable content, long callback) {
        if (null == context) {
            return;
        }
        Intent intent = new Intent(context, PlatformProxy.class);
        intent.putExtra(FLAG_CHANNEL, type);
        intent.putExtra(FLAG_BEHAVIOR, behavior);
        intent.putExtra(FLAG_CALLBACK, callback);
        if (null != content) {
            intent.putExtra(FLAG_CONTENT, content);
        }
        context.startActivity(intent);
        if (content instanceof Activity) {
            ((Activity) content).overridePendingTransition(0, 0);
        }
    }

    private static long getGlobCallbackHash() {
        return null == PlatformProxy.callback ? -1 : PlatformProxy.callback.hashCode();
    }

    private PlatformBehavior platformBehavior;

    private int behavior;
    private ChannelType channelType;
    private Serializable content;
    private long callbackHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        platformBehavior = new PlatformBehavior(this);
        super.onCreate(savedInstanceState);
        parseIntent();
        setContentView(getContentView());
        getWindow().getDecorView().setBackgroundColor(0);
        platformBehavior.onCreate(this, savedInstanceState);
        dispatchBehavior();
    }

    private void parseIntent() {
        final Intent intent = getIntent();
        channelType = (ChannelType) intent.getSerializableExtra(FLAG_CHANNEL);
        behavior = intent.getIntExtra(FLAG_BEHAVIOR, -1);
        callbackHash = intent.getLongExtra(FLAG_CALLBACK, -1);
        content = intent.getSerializableExtra(FLAG_CONTENT);
    }

    private View getContentView() {
        FrameLayout container = new FrameLayout(this);
        LinearLayout waiting = new LinearLayout(this);
        waiting.setBackgroundColor(Color.parseColor("#70000000"));
        final int padding = Tools.dip2px(this, 12);
        waiting.setPadding(padding, padding, padding, padding);
        waiting.setGravity(Gravity.CENTER);
        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        TextView tipsTxt = new TextView(this);
        tipsTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        tipsTxt.setTextColor(Color.LTGRAY);
        final String tipStr = behavior == BEHAVIOR_SHARE ? "正在分享" : (behavior == BEHAVIOR_LOGIN ? "正在登录" : "请稍等");
        tipsTxt.setText(String.format(Locale.PRC, " %s...", tipStr));
        waiting.addView(progress);
        waiting.addView(tipsTxt);
        container.addView(waiting, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        return container;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        platformBehavior.onNewIntent(this, intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        platformBehavior.onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        platformBehavior.onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        platformBehavior.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        platformBehavior.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        platformBehavior.onStop(this);
    }

    @Override
    protected void onDestroy() {
        platformBehavior.clear();
        super.onDestroy();
        platformBehavior.onDestroy(this);
        PlatformProxy.callback = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        platformBehavior.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void finishWithInfo(ChannelType type, Object object, String msg, int code) {
        if (null != PlatformProxy.callback && getGlobCallbackHash() == callbackHash) {
            PlatformProxy.callback.call(type, object, msg, code);
        }
        finish();
    }

    private void dispatchBehavior() {
        if (null == channelType) {
            return;
        }
        platformBehavior.select(channelType);
        switch (behavior) {
            case BEHAVIOR_SHARE: {
                if (null != content && content instanceof ShareContent) {
                    platformBehavior.share((ShareContent) content, new Callback<Object>() {
                        @Override
                        public void call(ChannelType type, Object obj, String msg, int code) {
                            finishWithInfo(type, obj, msg, code);
                        }
                    });
                } else {
                    finishWithInfo(channelType, null, null, Constants.Code.ERROR);
                }
                break;
            }
            case BEHAVIOR_PAY: {
                if (null != content && content instanceof PayInfo) {
                    platformBehavior.pay((PayInfo) content, new Callback<Object>() {
                        @Override
                        public void call(ChannelType type, Object obj, String msg, int code) {
                            finishWithInfo(type, obj, msg, code);
                        }
                    });
                } else {
                    finishWithInfo(channelType, null, null, Constants.Code.ERROR);
                }
                break;
            }
            case BEHAVIOR_LOGIN: {
                platformBehavior.login(new Callback<AccountInfo>() {
                    @Override
                    public void call(ChannelType type, AccountInfo obj, String msg, int code) {
                        finishWithInfo(type, obj, msg, code);
                    }
                });
                break;
            }
            case BEHAVIOR_GET_FRIENDS: {
                final Callback<List<AccountInfo>> callback = new Callback<List<AccountInfo>>() {
                    @Override
                    public void call(ChannelType type, List<AccountInfo> obj, String msg, int code) {
                        finishWithInfo(type, obj, msg, code);
                    }
                };
                platformBehavior.getFriends(callback);
                break;
            }
        }
    }

}
