package org.pinwheel.platformsdk.channel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.utils.HttpsUtils;
import org.pinwheel.platformsdk.utils.Tools;

import java.io.Serializable;
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
public final class TaobaoAuthActivity extends Activity {

    private static final String API_AUTH = "https://oauth.taobao.com/authorize?response_type=token&client_id=%s&view=wap";

    static final String ACTION_TAOBAO_RESULT = "org.pinwheel.platformsdk.ACTION_TAOBAO_RESULT";

    private ProgressBar progressBar;
    private WebView webView;
    private View errorView;

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            errorView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            checkAndGetOpenId(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            if (View.GONE == errorView.getVisibility()) {
                webView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            errorView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
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

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TaobaoAuthActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView("淘宝登录"));
        webView.loadUrl(String.format(Locale.PRC, API_AUTH, PlatformConfig.INSTANCE.getTaobaoKey()));
    }

    private View getContentView(String title) {
        final int dp6 = Tools.dip2px(this, 6);
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
        // title txt
        TextView titleTxt = new TextView(getApplicationContext());
        titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleTxt.setGravity(Gravity.CENTER);
        titleTxt.setTextColor(Color.DKGRAY);
        titleTxt.setText(title);
        titleBar.addView(closeBtn, new FrameLayout.LayoutParams(dp6 * 8, -1, Gravity.LEFT | Gravity.CENTER));
        titleBar.addView(titleTxt, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
        View divider = new View(getApplicationContext());
        divider.setBackgroundColor(Color.LTGRAY);
        // content
        FrameLayout contentLayout = new FrameLayout(getApplicationContext());
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, transition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        contentLayout.setLayoutTransition(transition);
        // progress
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        // error tips
        TextView errorView = new TextView(getApplicationContext());
        this.errorView = errorView;
        errorView.setGravity(Gravity.CENTER | Gravity.TOP);
        errorView.setTextColor(Color.GRAY);
        errorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        errorView.setText("页面加载失败\n点击重试");
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.isNetworkConnected(getApplicationContext())) {
                    webView.reload();
                }
            }
        });
        try {
            Drawable icon = getResources().getDrawable(android.R.drawable.ic_menu_myplaces);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            errorView.setCompoundDrawables(null, icon, null, null);
        } catch (Exception e) {
        }
        contentLayout.addView(errorView, new FrameLayout.LayoutParams(-2, dp6 * 20, Gravity.CENTER));
        contentLayout.addView(webView, -1, -1);
        contentLayout.addView(progressBar, new FrameLayout.LayoutParams(-1, dp6 / 3, Gravity.TOP));
        // root
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setBackgroundColor(Color.argb(255, 0xEF, 0xEF, 0xEF));
        container.setOrientation(LinearLayout.VERTICAL);
        //
        container.addView(titleBar, new LinearLayout.LayoutParams(-1, dp6 * 8));
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
//        final String key = "access_token=";
        final String key = "taobao_user_nick=";
        final int startIndex = url.indexOf(key);
        if (startIndex >= 0) {
            final int end = url.indexOf("&", startIndex);
            if (end > 0) {
                AccountInfo accountInfo = null;
                try {
                    String value = url.substring(startIndex + key.length(), end);
                    value = URLDecoder.decode(value, "utf-8");
                    accountInfo = new AccountInfo(ChannelType.TAOBAO);
                    accountInfo.id = value;// 淘宝WAP版免费api只能获取到昵称
                    accountInfo.nickName = value;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    onResult(null == accountInfo ? Constants.Code.ERROR : Constants.Code.SUCCESS, accountInfo);
                }
            } else {
                // auth error
                onResult(Constants.Code.ERROR_AUTH_DENIED, null);
            }
        }
    }

    private void onResult(int code, Serializable content) {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_CODE, code);
        if (null != content) {
            intent.putExtra(Constants.KEY_CONTENT, content);
        }
        intent.setAction(ACTION_TAOBAO_RESULT);
        sendBroadcast(intent);
        super.finish();
    }

    @Override
    public void finish() {
        onResult(Constants.Code.ERROR_CANCEL, null);
    }

    /**
     * 淘宝开放平台后台接口
     */
    private static class TaobaoApi {

        private static final String FIELDS = "nick,avatar";
        private static final String METHOD_GET_BUYER = "taobao.user.buyer.get";
        private static final String API_BASE_URL = "http://gw.api.taobao.com/router/rest";

        /**
         * 根据token换取用户信息，但此接口需要付费，暂不使用
         */
        public static AccountInfo getAccountInfo(String token) {
            Map<String, String> params = new TreeMap<>();
            // 参数顺序 a-z
            params.put("app_key", PlatformConfig.INSTANCE.getTaobaoKey());
            params.put("format", "json");
            params.put("fields", FIELDS);
            params.put("method", METHOD_GET_BUYER);
            params.put("sign_method", "md5");
            params.put("session", token);
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.PRC).format(new Date()));
            params.put("v", "2.0");
            params.put("sign", md5Signature(params, PlatformConfig.INSTANCE.getTaobaoSecret()));

            final String url = getUrlString(API_BASE_URL, params);
            String result = HttpsUtils.get(url, null);
            return null;
        }

        private static String getUrlString(String baseUrl, Map<String, String> params) {
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

        /**
         * 签名的参数除"sign"以外需要按照参数名字母a-z的顺序排序
         */
        private static String md5Signature(Map<String, String> params, String secret) {
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

        private static StringBuffer getBeforeSign(Map<String, String> params, StringBuffer orgin) {
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

        private static String byte2hex(byte[] b) {
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

}
