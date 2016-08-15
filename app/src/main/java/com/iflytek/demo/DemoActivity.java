package com.iflytek.demo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iflytek.platform.Platform;
import com.iflytek.platform.PlatformHelper;
import com.iflytek.platform.PlatformLifecycleWrapperActivity;
import com.iflytek.platform.R;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.List;

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
public class DemoActivity extends PlatformLifecycleWrapperActivity {

    private final View.OnClickListener shareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Platform.Type platform = getSelectedPlatform();
            if (null == platform) {
                return;
            }

            final ShareContent content = new ShareContent.Builder()
                    .title("share sdk")
                    .content("share from ichang")
                    .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                    .targetUrl("http://www.baidu.com")
                    .create();

            // share
            PlatformHelper.INSTANCE.share(DemoActivity.this, platform, content, new Callback() {
                @Override
                public void call(boolean isSuccess, String msg, int code) {
                    Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Platform.Type platform = getSelectedPlatform();
            if (null == platform) {
                return;
            }

            PlatformHelper.INSTANCE.login(DemoActivity.this, platform, new Callback2<AccountInfo>() {
                @Override
                public void call(AccountInfo accountInfo, boolean isSuccess, String msg, int code) {
                    Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final View.OnClickListener getFriendsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Platform.Type platform = getSelectedPlatform();
            if (null == platform) {
                return;
            }

            PlatformHelper.INSTANCE.getFriends(DemoActivity.this, platform, new Callback2<List<AccountInfo>>() {
                @Override
                public void call(List<AccountInfo> accountInfos, boolean isSuccess, String msg, int code) {
                    Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final View.OnClickListener payClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Platform.Type platform = getSelectedPlatform();
            if (null == platform) {
                return;
            }

            final PayInfo payInfo = new PayInfo();

            PlatformHelper.INSTANCE.pay(DemoActivity.this, platform, payInfo, new Callback() {
                @Override
                public void call(boolean isSuccess, String msg, int code) {
                    Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPlatformLifecycle(true);
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

    private Platform.Type getSelectedPlatform() {
        Platform.Type platform = null;
        ViewGroup platformNavi = (ViewGroup) findViewById(R.id.navi_platforms);
        final int size = platformNavi.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = platformNavi.getChildAt(i);
            if (v instanceof RadioButton) {
                if (((RadioButton) v).isChecked()) {
                    Object tag = v.getTag();
                    if (null != tag) {
                        platform = Platform.Type.valueOf(String.valueOf(tag));
                    }
                    break;
                }
            }
        }
        return platform;
    }

}
