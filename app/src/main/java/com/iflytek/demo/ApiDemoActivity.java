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
import com.iflytek.platform.PlatformTokenKeeper;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.utils.Tools;

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
public class ApiDemoActivity extends Activity {

    /**
     * 基础API调用接口
     */
    private PlatformBehavior platformBehavior;

    /**
     * 分享
     */
    private final View.OnClickListener shareClick = view -> {
        final ChannelType type = getSelectedType();
        final ShareContent content = new ShareContent.Builder()
                .title("share sdk")
                .content("share from ichang")
                .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                .targetUrl("http://www.baidu.com")
                .create();
        // 授权分享
//        platformBehavior.select(type).share(content, (channelType, obj, msg, code) -> {
//            // TODO: 2016/8/23
//            Toast.makeText(getApplicationContext(), Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
//        });
        // 静默分享
        platformBehavior.select(type).share(PlatformTokenKeeper.INSTANCE.getToken(type), content, new Callback<Object>() {
            @Override
            public void call(ChannelType type, Object o, String msg, int code) {
                Toast.makeText(getApplicationContext(), Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    };

    /**
     * 登录
     */
    private final View.OnClickListener loginClick = view -> {
        final ChannelType type = getSelectedType();
        platformBehavior.select(type).login((channelType, user, msg, code) -> {
            // TODO: 2016/8/23
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();

            PlatformTokenKeeper.INSTANCE.setToken(channelType, user.token);
        });
    };

    /**
     * 获取朋友列表
     */
    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        platformBehavior.select(type).getFriends((channelType, users, msg, code) -> {
            // TODO: 2016/8/23
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = users.size() + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 支付
     */
    private final View.OnClickListener payClick = view -> {
        final ChannelType type = getSelectedType();
        final PayInfo payInfo = new PayInfo();
        platformBehavior.select(type).pay(payInfo, (channelType, isSuccess, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), isSuccess + ", " + Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化基础API接口绑定Activity
        platformBehavior = new PlatformBehavior(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerListener();
        // 生命周期回调
        platformBehavior.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 生命周期回调
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

}