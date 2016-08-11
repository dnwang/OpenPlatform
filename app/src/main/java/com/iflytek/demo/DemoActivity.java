package com.iflytek.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iflytek.platform.PlatformManager;
import com.iflytek.platform.Platforms;
import com.iflytek.platform.callbacks.OnShareListener;
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

    static {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onClick(View view) {
        final String tag = String.valueOf(view.getTag());

        PlatformManager.INSTANCE.share(Platforms.WEIBO, new ShareContent(), new OnShareListener() {

            @Override
            public void onComplete(Platforms platform, boolean isSuccess, String msg) {

            }
        });

    }

}
