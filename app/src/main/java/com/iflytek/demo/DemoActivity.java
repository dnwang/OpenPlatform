package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.ihou.chang.app.R;
import com.iflytek.platform.PlatformHelper;
import com.iflytek.platform.PlatformType;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;

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

    private PlatformHelper platformHelper;

    private final View.OnClickListener shareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ShareContent content = new ShareContent.Builder()
                    .title("share sdk")
                    .content("share from ichang")
                    .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                    .targetUrl("http://www.baidu.com")
                    .create();
            platformHelper.select(getSelectedType()).share(content, (obj, msg, code) -> {
                Toast.makeText(getApplicationContext(), code + ", " + msg, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private final View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            platformHelper.select(getSelectedType()).login((user, msg, code) -> {
                String tips;
                if (StateCodes.SUCCESS == code) {
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
            platformHelper.select(getSelectedType()).getFriends((userList, msg, code) -> {
                Toast.makeText(getApplicationContext(), userList.size() + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private final View.OnClickListener payClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final PayInfo payInfo = new PayInfo();
            platformHelper.select(getSelectedType()).pay(payInfo, (isSuccess, msg, code) -> {
                Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        platformHelper = new PlatformHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerListener();
        platformHelper.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        platformHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    private void registerListener() {
        findViewById(R.id.btn_share).setOnClickListener(shareClick);
        findViewById(R.id.btn_login).setOnClickListener(loginClick);
        findViewById(R.id.btn_friends).setOnClickListener(getFriendsClick);
        findViewById(R.id.btn_pay).setOnClickListener(payClick);
    }

    private PlatformType getSelectedType() {
        PlatformType platform = null;
        ViewGroup platformNavi = (ViewGroup) findViewById(R.id.navi_platforms);
        final int size = platformNavi.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = platformNavi.getChildAt(i);
            if (v instanceof RadioButton) {
                if (((RadioButton) v).isChecked()) {
                    Object tag = v.getTag();
                    if (null != tag) {
                        platform = PlatformType.valueOf(String.valueOf(tag));
                    }
                    break;
                }
            }
        }
        return platform;
    }

}
