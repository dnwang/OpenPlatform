package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iflytek.platform.PlatformType;
import com.iflytek.platform.PlatformHelper;
import com.iflytek.platform.LifecycleDispatcher;
import com.iflytek.platform.Socialize;
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
public class DemoActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LifecycleDispatcher.onCreate(this, savedInstanceState);
        setContentView(getContentView());
    }

    private View getContentView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        final String[] titles = new String[]{"qq", "qzone", "weixin", "weixinfriend", "weibo"};
        for (String title : titles) {
            Button btn = new Button(this);
            btn.setText(title);
            btn.setTag(title);
            container.addView(btn, -1, -2);
        }
        return container;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LifecycleDispatcher.onNewIntent(this, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LifecycleDispatcher.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        final String tag = String.valueOf(view.getTag());

        PlatformHelper.INSTANCE.share(this, PlatformType.WEIBO, new ShareContent(), new Socialize.OnShareListener() {

            @Override
            public void onComplete(PlatformType platformType, boolean isSuccess, String msg) {

            }
        });

    }

}
