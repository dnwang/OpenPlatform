package org.pinwheel.platformsdk.channel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;

import java.io.Serializable;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 10/09/16,21:01
 * @see
 */
public final class SinaWeiboShareActivity extends Activity {

    static final String ACTION_WEIBO_RESULT = "org.pinwheel.platformsdk.ACTION_WEIBO_RESULT";

    public static void startActivity(Context context, ShareContent content) {
        Intent intent = new Intent(context, SinaWeiboShareActivity.class);
        intent.putExtra(Constants.KEY_CONTENT, content);
        context.startActivity(intent);
    }

    private ShareContent content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Intent intent = getIntent();
        Object obj = intent.getSerializableExtra(Constants.KEY_CONTENT);
        if (null != obj && obj instanceof ShareContent) {
            content = (ShareContent) obj;
        } else {
            finish();
            return;
        }
        setContentView(getContentView("微博分享"));
    }

    private View getContentView(String title) {
        // title bar
        FrameLayout titleBar = new FrameLayout(getApplicationContext());
        // close button
        TextView closeBtn = new TextView(getApplicationContext());
        closeBtn.setText("关闭");
        closeBtn.setTextColor(Color.GRAY);
        closeBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        closeBtn.setGravity(Gravity.CENTER);
        closeBtn.setBackgroundColor(0);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // title txt
        TextView titleTxt = new TextView(getApplicationContext());
        titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleTxt.setGravity(Gravity.CENTER);
        titleTxt.setTextColor(Color.DKGRAY);
        titleTxt.setText(title);
        titleBar.addView(closeBtn, new FrameLayout.LayoutParams(Tools.dip2px(this, 48), -1, Gravity.LEFT | Gravity.CENTER));
        titleBar.addView(titleTxt, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
        View divider = new View(getApplicationContext());
        divider.setBackgroundColor(Color.LTGRAY);
        // content
        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, transition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        contentLayout.setLayoutTransition(transition);

        EditText contentEdt = new EditText(getApplicationContext());
        contentEdt.setText(content.content);

        contentLayout.addView(contentEdt, -1, -1);
        // root
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setBackgroundColor(Color.WHITE);
        container.setOrientation(LinearLayout.VERTICAL);
        //
        container.addView(titleBar, new LinearLayout.LayoutParams(-1, Tools.dip2px(this, 48)));
        container.addView(divider, new LinearLayout.LayoutParams(-1, 1));
        container.addView(contentLayout, new LinearLayout.LayoutParams(-1, 0, 1));
        return container;
    }

    private void onResult(int code, Serializable content) {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_CODE, code);
        if (null != content) {
            intent.putExtra(Constants.KEY_CONTENT, content);
        }
        intent.setAction(ACTION_WEIBO_RESULT);
        sendBroadcast(intent);
        super.finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_CODE, Constants.Code.ERROR_CANCEL);
        setResult(RESULT_OK, intent);
        super.finish();
    }

}
