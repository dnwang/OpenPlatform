package com.iflytek.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.ihou.chang.app.R;
import com.iflytek.platform.channel.ChannelType;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.utils.Tools;
import com.iflytek.platform.wrapper.PlatformProxy;

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
public class ProxyDemoActivity extends Activity {

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
        PlatformProxy.share(ProxyDemoActivity.this, type, content, (channelType, obj, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
//        PlatformProxy.share(DemoWrapperActivity.this, type, content, new Callback<Object>() {
//            @Override
//            public void call(ChannelType type, Object o, String msg, int code) {
//
//            }
//        });
    };

    /**
     * 登录
     */
    private final View.OnClickListener loginClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.login(ProxyDemoActivity.this, type, (channelType, user, msg, code) -> {
            // TODO: 2016/8/23
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 获取朋友列表
     */
    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.getFriends(ProxyDemoActivity.this, type, (channelType, users, msg, code) -> {
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
        PlatformProxy.pay(ProxyDemoActivity.this, type, payInfo, (channelType, isSuccess, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), isSuccess + ", " + Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
