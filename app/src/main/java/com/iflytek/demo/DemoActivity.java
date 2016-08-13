package com.iflytek.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iflytek.platform.Platform;
import com.iflytek.platform.PlatformHelper;
import com.iflytek.platform.callbacks.Callback;
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
        // lifecycle back
        PlatformHelper.INSTANCE.onCreate(this, savedInstanceState);
        setContentView(getContentView());
    }

    private View getContentView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        for (Platform.Type type : Platform.Type.values()) {
            Button btn = new Button(this);
            btn.setOnClickListener(this);
            btn.setText(String.valueOf(type));
            btn.setTag(type);
            container.addView(btn, -1, -2);
        }
        return container;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // lifecycle back
        PlatformHelper.INSTANCE.onNewIntent(this, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // lifecycle callback
        PlatformHelper.INSTANCE.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        final Platform.Type type = (Platform.Type) view.getTag();

        final ShareContent content = new ShareContent();
        content.content = "test share message, fuck";
        content.imageUrl = "http://www.weipet.cn/common/images/pic/a347.jpg";
        content.title = "Share SDK";

        // share
        PlatformHelper.INSTANCE.share(this, type, new ShareContent(), new Callback() {
            @Override
            public void call(boolean isSuccess, String msg, int code) {
                Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
