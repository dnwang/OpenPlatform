package org.pinwheel.platformsdk.channel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
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

import java.util.Locale;

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

    private static final int MAX_EDIT_SIZE = 140;

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
        final int contentMaxSize = getMaxEditSize();

        final int dp6 = Tools.dip2px(this, 6);
        // content edit
        final EditText contentEdt = new EditText(getApplicationContext());
        contentEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(contentMaxSize)});
        contentEdt.setHint("分享新鲜事...");
        contentEdt.setHintTextColor(Color.GRAY);
        contentEdt.setGravity(Gravity.LEFT | Gravity.TOP);
        contentEdt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        contentEdt.setTextColor(Color.DKGRAY);
        contentEdt.setBackgroundColor(Color.TRANSPARENT);
        contentEdt.setText(shareContent.content);
        contentEdt.setPadding(0, 0, 0, 0);
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

        //
        LinearLayout editContainer = new LinearLayout(getApplicationContext());
        editContainer.setPadding(dp6, dp6, dp6, dp6);
        editContainer.setOrientation(LinearLayout.VERTICAL);
        editContainer.setBackgroundColor(Color.WHITE);
        TextView tipsTxt = new TextView(getApplicationContext());
        tipsTxt.setText(String.format(Locale.PRC, "最多输入%d个汉字", contentMaxSize));
        tipsTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        tipsTxt.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams tipsTxtParams = new LinearLayout.LayoutParams(-2, -2);
        tipsTxtParams.gravity = Gravity.RIGHT;
        editContainer.addView(contentEdt, -1, dp6 * 28);
        editContainer.addView(tipsTxt, tipsTxtParams);
        LinearLayout.LayoutParams editContainerParams = new LinearLayout.LayoutParams(-1, -2);
        editContainerParams.setMargins(dp6, dp6 * 2, dp6, dp6);

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

        // attachment container
        LinearLayout attachmentContainer = new LinearLayout(getApplicationContext());
        attachmentContainer.setOrientation(LinearLayout.HORIZONTAL);
        attachmentContainer.setPadding(dp6, dp6, dp6, dp6);
        attachmentContainer.setGravity(Gravity.CENTER);
        // link text
        LinearLayout linkContainer = new LinearLayout(getApplicationContext());
        linkContainer.setPadding(0, 0, dp6, 0);
        linkContainer.setOrientation(LinearLayout.VERTICAL);
        linkContainer.addView(createLinkTxt(shareContent.linkUrl), -1, -2);
        linkContainer.addView(createLinkTxt(shareContent.mediaUrl), -1, -2);

        attachmentContainer.addView(linkContainer, new LinearLayout.LayoutParams(0, -2, 1));
        attachmentContainer.addView(imageView, new LinearLayout.LayoutParams(dp6 * 10, dp6 * 10));

        // content
        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, transition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        contentLayout.setLayoutTransition(transition);

        contentLayout.addView(editContainer, editContainerParams);
        contentLayout.addView(attachmentContainer);
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

    private TextView createLinkTxt(String link) {
        final int dp4 = Tools.dip2px(this, 4);
        TextView textView = new TextView(getApplicationContext());
        textView.setPadding(0, 0, dp4, dp4);
        textView.setGravity(Gravity.CENTER | Gravity.LEFT);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textView.setTextColor(Color.LTGRAY);
        textView.setEllipsize(TextUtils.TruncateAt.END);
//        Drawable linkIcon = getResources().getDrawable(android.R.drawable.);
//        linkIcon.setBounds(0, 0, linkIcon.getMinimumWidth(), linkIcon.getMinimumHeight());
//        textView.setCompoundDrawables(linkIcon, null, null, null);
        textView.setText(link);
        if (TextUtils.isEmpty(link)) {
            textView.setVisibility(View.GONE);
        }
        return textView;
    }

    private int getMaxEditSize() {
        if (null == shareContent) {
            return MAX_EDIT_SIZE;
        }
        int linkUrlSize = TextUtils.isEmpty(shareContent.linkUrl) ? 0 : shareContent.linkUrl.length() / 2;
        int mediaUrlSize = TextUtils.isEmpty(shareContent.mediaUrl) ? 0 : shareContent.mediaUrl.length() / 2;
        return MAX_EDIT_SIZE - linkUrlSize - mediaUrlSize;
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
