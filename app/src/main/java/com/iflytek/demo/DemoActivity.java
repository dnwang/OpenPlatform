package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iflytek.platform.Platform;
import com.iflytek.platform.PlatformHelper;
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
        setContentView(getContentView());

        PlatformHelper.INSTANCE.initialize(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PlatformHelper.INSTANCE.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PlatformHelper.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }

    private View getContentView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        final String[] titles = new String[]{"qq", "qzone", "weixin", "weixinfriend", "weibo"};
        for (String title : titles) {
            Button btn = new Button(this);
            btn.setOnClickListener(this);
            btn.setText(title);
            btn.setTag(title);
            container.addView(btn, -1, -2);
        }
        return container;
    }

    @Override
    public void onClick(View view) {
        final String tag = String.valueOf(view.getTag());

        PlatformHelper.INSTANCE.getPlatform(Platform.Type.WEIBO).share(this, new ShareContent(), new Platform.Callback() {
            @Override
            public void call(boolean isSuccess, String msg, int code) {

            }
        });

    }

}
