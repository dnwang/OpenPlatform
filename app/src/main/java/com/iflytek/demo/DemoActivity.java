package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.ihou.chang.app.R;
import com.iflytek.platform.PlatformBehavior;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

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
public class DemoActivity extends Activity {

    private PlatformBehavior platformBehavior;

    private final View.OnClickListener shareClick = view -> {
        final ChannelType type = getSelectedType();
        final ShareContent content = new ShareContent.Builder()
                .title("share sdk")
                .content("share from ichang")
                .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                .targetUrl("http://www.baidu.com")
                .create();
        platformBehavior.select(type).share(content, (obj, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), covertCode(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener loginClick = view -> {
        final ChannelType type = getSelectedType();
        platformBehavior.select(type).login((user, msg, code) -> {
            // TODO: 2016/8/23
            String tips = covertCode(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        platformBehavior.select(type).getFriends((users, msg, code) -> {
            // TODO: 2016/8/23
            String tips = covertCode(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = users.size() + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener payClick = view -> {
        final ChannelType type = getSelectedType();
        final PayInfo payInfo = new PayInfo();
        platformBehavior.select(type).pay(payInfo, (isSuccess, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), isSuccess + ", " + covertCode(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        platformBehavior = new PlatformBehavior(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerListener();
        platformBehavior.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        platformBehavior.onActivityResult(this, requestCode, resultCode, data);
    }

    private void registerListener() {
        findViewById(R.id.btn_share).setOnClickListener(shareClick);
        findViewById(R.id.btn_login).setOnClickListener(loginClick);
        findViewById(R.id.btn_friends).setOnClickListener(getFriendsClick);
        findViewById(R.id.btn_pay).setOnClickListener(payClick);
    }

    private ChannelType getSelectedType() {
        ChannelType channelType = null;
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.navi_platforms);
        final int size = viewGroup.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof RadioButton) {
                if (((RadioButton) v).isChecked()) {
                    Object tag = v.getTag();
                    if (null != tag) {
                        channelType = ChannelType.valueOf(String.valueOf(tag));
                    }
                    break;
                }
            }
        }
        return channelType;
    }


    private static String covertCode(int code) {
        switch (code) {
            case Constants.Code.ERROR:
                return "error";
            case Constants.Code.ERROR_AUTH_DENIED:
                return "auth error";
            case Constants.Code.ERROR_CANCEL:
                return "cancel";
            case Constants.Code.ERROR_LOGIN:
                return "login error";
            case Constants.Code.ERROR_NOT_INSTALL:
                return "not install app";
            case Constants.Code.ERROR_NOT_SUPPORT:
                return "not support";
            case Constants.Code.SUCCESS:
                return "success";
            default:
                return "unknown";
        }
    }

}
