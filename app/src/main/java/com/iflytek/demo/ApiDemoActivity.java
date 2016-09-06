package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.ihou.chang.app.R;
import com.iflytek.platform.PlatformBehavior;
import com.iflytek.platform.PlatformTokenKeeper;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.AccessToken;
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
                .image(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .linkUrl("http://172.16.4.3/group1/M00/01/AF/rBAEA1bUaTmAHc7GAIrbl2MfKI8642.mp3")
                .mediaUrl("http://172.16.4.196:8080/share/df917a22bd1f4c7380340cab94af2061.shtml")
                .create();
        // 授权分享
//        platformBehavior.select(type).share(content, (channelType, obj, msg, code) -> {
//            Toast.makeText(getApplicationContext(), Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
//        });
        // 静默分享
        AccessToken accessToken = PlatformTokenKeeper.INSTANCE.getToken(type);
        platformBehavior.select(type).share(accessToken, content, (channelType, o, msg, code) -> {
            if (Constants.Code.ERROR_AUTH_DENIED == code) {
                Toast.makeText(getApplicationContext(), "silent share test.\nplease login first!", Toast.LENGTH_SHORT).show();
            } else {
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
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
            if (Constants.Code.SUCCESS == code) {
                PlatformTokenKeeper.INSTANCE.setToken(channelType, user.token);
            }
        });
    };

    /**
     * 获取朋友列表
     */
    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        platformBehavior.select(type).getFriends((channelType, users, msg, code) -> {
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
