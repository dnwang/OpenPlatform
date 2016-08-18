package com.iflytek.platform.callbacks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.utils.HttpsUtils;
import com.umeng.socialize.utils.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    private static final String FIELDS = "nick,avatar";
    private static final String METHOD_GET_BUYER = "taobao.user.buyer.get";

    private static final String API_AUTH = "https://oauth.taobao.com/authorize?response_type=token&client_id=%s&view=wap";
    private static final String API_GET_USER = "http://gw.api.taobao.com/router/rest";
//    private static final String API_GET_USER = "https://eco.taobao.com/router/rest";

    public static final int REQ_TAOBAO = 0x863;

    private ProgressBar progressBar;
    private WebView webView;

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            checkAndGetOpenId(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO: 2016/8/18
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();// support https url
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };

    public static void startActivity(Activity activity) {
        activity.startActivityForResult(new Intent(activity.getApplicationContext(), TaobaoAuthActivity.class), REQ_TAOBAO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        webView.loadUrl(String.format(Locale.PRC, API_AUTH, APP_KEY));
    }

    private View getContentView() {
        // web view
        webView = new WebView(getApplicationContext());
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

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

    @Override
    public void onBackPressed() {
        if (null != webView && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void checkAndGetOpenId(String url) {
//        final String key = "taobao_user_nick=";
        final String key = "access_token=";
        final int startIndex = url.indexOf(key);
        if (startIndex >= 0) {
            final int end = url.indexOf("&", startIndex);
            if (end > 0) {
                String value = url.substring(startIndex + key.length(), end);
                try {
                    value = URLDecoder.decode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final String session = value;
                new Thread() {
                    @Override
                    public void run() {
                        AccountInfo accountInfo = getAccountInfo(session);
                        if (null != accountInfo) {
                            // success
                        } else {
                            // error
                        }
                    }
                }.start();
            } else {
                // auth error
            }
        }
    }

    private AccountInfo getAccountInfo(String session) {
        Map<String, String> params = new TreeMap<>();
        // 参数顺序 a-z
        params.put("app_key", APP_KEY);
        params.put("format", "json");
        params.put("fields", FIELDS);
        params.put("method", METHOD_GET_BUYER);
        params.put("sign_method", "md5");
        params.put("session", session);
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.PRC).format(new Date()));
        params.put("v", "2.0");
        params.put("sign", md5Signature(params, APP_SECRET));

        final String url = getUrlString(API_GET_USER, params);
        Log.e("--- taobao ---", String.valueOf(url));
        String result = HttpsUtils.get(url);
        Log.e("--- taobao ---", String.valueOf(result));
        return null;
    }

    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private String getUrlString(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder url = new StringBuilder(baseUrl);
        if (!baseUrl.contains("?")) {
            url.append("?");
        }
        Set<Map.Entry<String, String>> set = params.entrySet();
        for (Map.Entry<String, String> entry : set) {
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return url.toString().replace("?&", "?");
    }

    private String md5Signature(Map<String, String> params, String secret) {
        String result = null;
        StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));
        if (orgin == null) {
            return result;
        }
        orgin.append(secret);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
        } catch (Exception e) {
            throw new java.lang.RuntimeException("sign error !");
        }
        return result;
    }

    private StringBuffer getBeforeSign(Map<String, String> params, StringBuffer orgin) {
        if (params == null) {
            return null;
        }
        Map<String, String> map = new TreeMap<>();
        map.putAll(params);
        for (String name : map.keySet()) {
            orgin.append(name).append(params.get(name));
        }
        return orgin;
    }

    private String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (byte aB : b) {
            stmp = (Integer.toHexString(aB & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

}
