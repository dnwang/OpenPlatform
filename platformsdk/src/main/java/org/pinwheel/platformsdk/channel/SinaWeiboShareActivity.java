package org.pinwheel.platformsdk.channel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;

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

    private ShareContent shareContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Intent intent = getIntent();
        Object obj = intent.getSerializableExtra(Constants.KEY_CONTENT);
        if (null != obj && obj instanceof ShareContent) {
            shareContent = (ShareContent) obj;
        } else {
            finish();
            return;
        }
        setContentView(getContentView("微博分享"));
    }

    private View getContentView(String title) {
        final int dp6 = Tools.dip2px(this, 6);
        // content edit
        final EditText contentEdt = new EditText(getApplicationContext());
        contentEdt.setMaxEms(140);
        contentEdt.setHint("说点什么吧...");
        contentEdt.setGravity(Gravity.LEFT | Gravity.TOP);
        contentEdt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        contentEdt.setTextColor(Color.DKGRAY);
        contentEdt.setBackgroundColor(Color.WHITE);
        contentEdt.setText(shareContent.content);
        // title bar
        FrameLayout titleBar = new FrameLayout(getApplicationContext());
        titleBar.setBackgroundColor(Color.WHITE);
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
        // share button
        TextView shareBtn = new TextView(getApplicationContext());
        shareBtn.setText("分享");
        shareBtn.setTextColor(Color.argb(255, 0xFC, 0x6C, 0x08));
        shareBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        shareBtn.setGravity(Gravity.CENTER);
        shareBtn.setBackgroundColor(0);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent.content = contentEdt.getText().toString();
                onResult(Constants.Code.SUCCESS);
            }
        });

        // title txt
        TextView titleTxt = new TextView(getApplicationContext());
        titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleTxt.setGravity(Gravity.CENTER);
        titleTxt.setTextColor(Color.DKGRAY);
        titleTxt.setText(title);
        titleBar.addView(closeBtn, new FrameLayout.LayoutParams(dp6 * 8, -1, Gravity.LEFT | Gravity.CENTER));
        titleBar.addView(shareBtn, new FrameLayout.LayoutParams(dp6 * 8, -1, Gravity.RIGHT | Gravity.CENTER));
        titleBar.addView(titleTxt, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));

        View divider = new View(getApplicationContext());
        divider.setBackgroundColor(Color.LTGRAY);
        // content
        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, transition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        contentLayout.setLayoutTransition(transition);
        // image
        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (null == shareContent.image) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            ContentConverter.getBitmap(getResources(), shareContent.image, new SimpleListener<Bitmap>() {
                @Override
                public void call(final Bitmap bitmap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        }
        //
        LinearLayout.LayoutParams contentEdtParams = new LinearLayout.LayoutParams(-1, dp6 * 25);
        contentEdtParams.setMargins(dp6, dp6 * 2, dp6, dp6);
        contentLayout.addView(contentEdt, contentEdtParams);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dp6 * 10, dp6 * 10);
        imageParams.setMargins(dp6, dp6, dp6, dp6);
        imageParams.gravity = Gravity.LEFT;
        contentLayout.addView(imageView, imageParams);
        // root view
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setBackgroundColor(Color.argb(255, 0xEF, 0xEF, 0xEF));
        container.setOrientation(LinearLayout.VERTICAL);
        //
        container.addView(titleBar, new LinearLayout.LayoutParams(-1, dp6 * 8));
        container.addView(divider, new LinearLayout.LayoutParams(-1, 1));
        container.addView(contentLayout, new LinearLayout.LayoutParams(-1, 0, 1));
        return container;
    }

    private void onResult(int code) {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_CODE, code);
        if (null != shareContent) {
            intent.putExtra(Constants.KEY_CONTENT, shareContent);
        }
        intent.setAction(ACTION_WEIBO_RESULT);
        sendBroadcast(intent);
        super.finish();
    }

    @Override
    public void finish() {
        onResult(Constants.Code.ERROR_CANCEL);
    }

}
