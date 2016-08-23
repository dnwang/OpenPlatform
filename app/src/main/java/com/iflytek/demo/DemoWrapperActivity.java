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
public class DemoWrapperActivity extends Activity {

    private final View.OnClickListener shareClick = view -> {
        final ChannelType type = getSelectedType();
        final ShareContent content = new ShareContent.Builder()
                .title("share sdk")
                .content("share from ichang")
                .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                .targetUrl("http://www.baidu.com")
                .create();
        PlatformProxy.share(DemoWrapperActivity.this, type, content, (obj, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), code + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener loginClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.login(DemoWrapperActivity.this, type, (user, msg, code) -> {
            // TODO: 2016/8/23
            String tips;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + code + ", " + msg;
            } else {
                tips = code + ", " + msg;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.getFriends(DemoWrapperActivity.this, type, (users, msg, code) -> {
            // TODO: 2016/8/23
            String tips;
            if (Constants.Code.SUCCESS == code) {
                tips = users.size() + ", " + code + ", " + msg;
            } else {
                tips = code + ", " + msg;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    private final View.OnClickListener payClick = view -> {
        final ChannelType type = getSelectedType();
        final PayInfo payInfo = new PayInfo();
        PlatformProxy.pay(DemoWrapperActivity.this, type, payInfo, (isSuccess, msg, code) -> {
            // TODO: 2016/8/23
            Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerListener();
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
