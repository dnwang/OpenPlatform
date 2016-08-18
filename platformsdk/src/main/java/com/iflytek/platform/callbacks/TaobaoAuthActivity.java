package com.iflytek.platform.callbacks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/18,10:41
 * @see
 */
public class TaobaoAuthActivity extends Activity {

    private static final String APP_KEY = "23138012";
    private static final String APP_SECRET = "166503770b46f1abdbfc390e655cf283";

    private static final String API_AUTH = "https://oauth.taobao.com/authorize?response_type=token&client_id=%s&view=wap";

    public static final int REQ_TAOBAO = 0x863;

    private ProgressBar progressBar;
    private WebView webView;

    public static void startActivity(Activity activity) {
        activity.startActivityForResult(new Intent(activity.getApplicationContext(), TaobaoAuthActivity.class), REQ_TAOBAO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        webView.loadUrl(String.format(Locale.PRC, API_AUTH, APP_KEY));
//        webView.loadUrl("http://www.baidu.com");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
    }

    private View getContentView() {
        // web view
        webView = new WebView(getApplicationContext());
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // title
        FrameLayout titleBar = new FrameLayout(getApplicationContext());
        // close btn
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
        titleTxt.setText("淘宝登录");
        titleBar.addView(closeBtn, new FrameLayout.LayoutParams(dip2px(48), -1, Gravity.LEFT | Gravity.CENTER));
        titleBar.addView(titleTxt, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
        View divider = new View(getApplicationContext());
        divider.setBackgroundColor(Color.LTGRAY);

        // content
        FrameLayout contentLayout = new FrameLayout(getApplicationContext());

        // progress
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        contentLayout.addView(webView, -1, -1);
        contentLayout.addView(progressBar, new FrameLayout.LayoutParams(-1, dip2px(2), Gravity.TOP));
        // root
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setBackgroundColor(Color.WHITE);
        container.setOrientation(LinearLayout.VERTICAL);

        container.addView(titleBar, new LinearLayout.LayoutParams(-1, dip2px(48)));
        container.addView(divider, new LinearLayout.LayoutParams(-1, 1));
        container.addView(contentLayout, new LinearLayout.LayoutParams(-1, 0, 1));
        return container;
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            // TODO: 2016/8/18
            Toast.makeText(getApplicationContext(), "加载出错", Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }
    }

    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
