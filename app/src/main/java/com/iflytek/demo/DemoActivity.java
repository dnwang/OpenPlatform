package com.iflytek.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iflytek.platform.Platform;
import com.iflytek.platform.PlatformHelper;
import com.iflytek.platform.PlatformLifecycleWrapperActivity;
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
public class DemoActivity extends PlatformLifecycleWrapperActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPlatformLifecycle(true);
        super.onCreate(savedInstanceState);
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
    public void onClick(View view) {
        final Platform.Type type = (Platform.Type) view.getTag();

        final ShareContent content = new ShareContent.Builder()
                .title("share sdk")
                .content("share from ichang")
                .imageUrl("http://www.weipet.cn/common/images/pic/a347.jpg")
                .targetUrl("http://www.baidu.com")
                .create();

        // share
        PlatformHelper.INSTANCE.share(this, type, content, new Callback() {
            @Override
            public void call(boolean isSuccess, String msg, int code) {
                Toast.makeText(getApplicationContext(), isSuccess + ", " + code + ", " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
