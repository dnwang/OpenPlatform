package com.iflytek.platform.callbacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.iflytek.platform.utils.HttpUtils;
import com.iflytek.platform.utils.Tools;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/16,14:21
 * @see
 */
public abstract class AbsWeixinApiActivity extends Activity implements IWXAPIEventHandler {

    private static final String APP_ID = "wxf04bacbcee9b5cc7";
    private static final String APP_SECRET = "9299bfd1ec0104a4cad2faa23010a580";
    private static final String CLASS_WXAPI = ".wxapi.WXEntryActivity";// 固定api类名，必须存在

    public static final String FLAG_CODE = "code";
    public static final String FLAG_TYPE = "type";
    public static final String FLAG_CONTENT = "content";

    /**
     * 分享到好友
     */
    public static final int TYPE_SHARE_FRIEND = 100;
    /**
     * 分享到朋友圈
     */
    public static final int TYPE_SHARE_CIRCLE = 101;
    /**
     * 授权登录
     */
    public static final int TYPE_LOGIN = 200;

    public static final int REQ_WEIXIN = 0x763;

    private IWXAPI wxApi;
    private int type;
    private Object content;

    public static boolean startActivity(Activity activity, int type, Serializable content) {


        Class wxApiActivity = getWXApiActivity(activity);
        if (null == wxApiActivity) {
            return false;
        }
        Intent intent = new Intent(activity, wxApiActivity);
        intent.putExtra(FLAG_TYPE, type);
        intent.putExtra(FLAG_CONTENT, content);
        activity.startActivityForResult(intent, REQ_WEIXIN);
        activity.overridePendingTransition(0, 0);
        return true;
    }

    private static Class getWXApiActivity(Context context) {
        Class wxApiActivity = null;
        try {
            wxApiActivity = Class.forName(context.getPackageName() + CLASS_WXAPI);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return wxApiActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        getWindow().getDecorView().setBackgroundColor(0);
        wxApi = WXAPIFactory.createWXAPI(this, APP_ID, false);
        wxApi.handleIntent(getIntent(), this);
        parseIntent();
        dispatchEvent();
        finishInResume = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxApi.handleIntent(intent, this);
        finishInResume = false;
    }

    private View getContentView() {
        FrameLayout container = new FrameLayout(this);
        TextView tips = new TextView(this);
        tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tips.setShadowLayer(5, 0, 0, Color.DKGRAY);
        tips.setTextColor(Color.WHITE);
        tips.setText("请稍等...");
        container.addView(tips, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        return container;
    }

    private void parseIntent() {
        Intent intent = getIntent();
        type = intent.getIntExtra(FLAG_TYPE, -1);
        content = intent.getSerializableExtra(FLAG_CONTENT);
    }

    private boolean finishInResume = true;

    @Override
    protected void onResume() {
        super.onResume();
        // 没有安装微信 || 点击"留在微信中"之后再返回要关闭
        if (!wxApi.isWXAppInstalled() || finishInResume) {
            finish();
        }
        finishInResume = true;
    }

    private void dispatchEvent() {
        switch (type) {
            case TYPE_SHARE_FRIEND: {
                if (null != content) {
                    share2Session((ShareContent) content);
                }
                break;
            }
            case TYPE_SHARE_CIRCLE: {
                if (null != content) {
                    share2Timeline((ShareContent) content);
                }
                break;
            }
            case TYPE_LOGIN: {
                login();
                break;
            }
        }
    }

    private void share2Session(ShareContent shareContent) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = shareContent.content;
        WXMediaMessage message = new WXMediaMessage(textObject);
        message.description = shareContent.title;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
    }

    private void share2Timeline(ShareContent shareContent) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = shareContent.content;
        WXMediaMessage message = new WXMediaMessage(textObject);
        message.description = shareContent.title;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private void login() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "ichang_wx_auth";
        wxApi.sendReq(req);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK: {
                if (TYPE_LOGIN == type) {
                    if (!(resp instanceof SendAuth.Resp)) {
                        onResult(StateCodes.ERROR, null);
                        return;
                    }
                    final String code = ((SendAuth.Resp) resp).token;
                    if (TextUtils.isEmpty(code)) {
                        onResult(StateCodes.ERROR, null);
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            final AccountInfo accountInfo = getUserInfo(code);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onResult(null == accountInfo ? StateCodes.ERROR : StateCodes.SUCCESS, accountInfo);
                                }
                            });
                        }
                    }.start();
                } else {
                    // other type
                    onResult(StateCodes.SUCCESS, null);
                }
                break;
            }
            case BaseResp.ErrCode.ERR_USER_CANCEL: {
                onResult(StateCodes.ERROR_CANCEL, null);
                break;
            }
            case BaseResp.ErrCode.ERR_AUTH_DENIED: {
                onResult(StateCodes.ERROR_AUTH_DENIED, null);
                break;
            }
            default: {
                onResult(StateCodes.ERROR, null);
                break;
            }
        }
    }

    private AccountInfo getUserInfo(final String code) {
        final String url4Token = String.format(Locale.PRC, "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", APP_ID, APP_SECRET, code);
        // get token info
        final String tokenInfo = HttpUtils.get(url4Token);
        try {
            // tokenInfo:{access_token,expires_in,refresh_token,openid,scope,unionid}
            JSONObject json = new JSONObject(tokenInfo);
            final String assessToken = Tools.getJsonString(json, "access_token");
            final String openId = Tools.getJsonString(json, "openid");
            // get user info
            final String url4User = String.format(Locale.PRC, "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s", assessToken, openId);
            final String userInfo = HttpUtils.get(url4User);
            return toAccountInfo(userInfo);
        } catch (Exception e) {
            return null;
        }
    }

    private void onResult(int code, Serializable content) {
        Intent intent = getIntent();
        intent.putExtra(FLAG_CODE, code);
        if (null != content) {
            intent.putExtra(FLAG_CONTENT, content);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private AccountInfo toAccountInfo(String userInfo) throws Exception {
        // {errcode}
        // user:{subscribe,openid,nickname,sex,language,city,province,country,headimgurl,subscribe_time}
        JSONObject json = new JSONObject(userInfo);
        if (!TextUtils.isEmpty(Tools.getJsonString(json, "errcode"))) {
            throw new RuntimeException(userInfo);
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.nickName = Tools.getJsonString(json, "nickname");
        accountInfo.headerImg = Tools.getJsonString(json, "headimgurl");
        accountInfo.gender = Tools.getJsonInt(json, "sex", 0) == 1;
        accountInfo.putExtra("openId", Tools.getJsonString(json, "openid"));
        accountInfo.putExtra("city", Tools.getJsonString(json, "city"));
        accountInfo.putExtra("province", Tools.getJsonString(json, "province"));
        accountInfo.putExtra("country", Tools.getJsonString(json, "country"));
        return accountInfo;
    }

}
