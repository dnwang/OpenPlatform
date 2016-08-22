package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.ihou.chang.app.R;
import com.iflytek.platform.PlatformConfig;
import com.iflytek.platform.PlatformBehavior;
import com.iflytek.platform.ChannelType;
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

    static {
        PlatformConfig.INSTANCE.setWeixin("wxf04bacbcee9b5cc7", "9299bfd1ec0104a4cad2faa23010a580");
        PlatformConfig.INSTANCE.setSina("778164658", "06552db3dc303529ba971b257379c49e");
        PlatformConfig.INSTANCE.setTencent("100526240", "20bca3e9e564042b7d1e2ec6ee261b1c");
        PlatformConfig.INSTANCE.setTaobao("23138012","166503770b46f1abdbfc390e655cf283");
    }

    private PlatformBehavior platformBehavior;

    private final View.OnClickListener shareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ShareContent content = new ShareContent.Builder()
                    .title("share sdk")
                    .content("share from ichang")
                    .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                    .targetUrl("http://www.baidu.com")
                    .create();
            platformBehavior.select(getSelectedType()).share(content, (obj, msg, code) -> {
                Toast.makeText(getApplicationContext(), code + ", " + msg, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private final View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            platformBehavior.select(getSelectedType()).login((user, msg, code) -> {
                String tips;
                if (Constants.Code.SUCCESS == code) {
                    tips = user.id + ", " + code + ", " + msg;
                } else {
                    tips = code + ", " + msg;
                }
                Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private final View.OnClickListener getFriendsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            platformBehavior.select(getSelectedType()).getFriends((users, msg, code) -> {
                String tips;
                if (Constants.Code.SUCCESS == code) {
                    tips = users.size() + ", " + code + ", " + msg;
                } else {
                    tips = code + ", " + msg;
                }
                Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private final View.OnClickListener payClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final PayInfo payInfo = new PayInfo();
            platformBehavior.select(getSelectedType()).pay(payInfo, (isSuccess, msg, code) -> {
                Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
            });
        }
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

}
