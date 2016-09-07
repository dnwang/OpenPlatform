package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.SimpleListener;
import org.pinwheel.platformsdk.entity.AccessToken;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.HttpsUtils;
import org.pinwheel.platformsdk.utils.Tools;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
public abstract class WeixinAuthActivity extends Activity implements IWXAPIEventHandler {

    private static final String CLASS_WXAPI = ".wxapi.WXEntryActivity";// 固定api类名，必须存在

    private static final String FLAG_TYPE = "type";
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
        if (type < 0) {
            return false;
        }
        Class wxApiActivity = getWXApiActivity(activity);
        if (null == wxApiActivity) {
            return false;
        }
        Intent intent = new Intent(activity, wxApiActivity);
        intent.putExtra(FLAG_TYPE, type);
        intent.putExtra(Constants.KEY_CONTENT, content);
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(getContentView());
        getWindow().getDecorView().setBackgroundColor(0);
        wxApi = WXAPIFactory.createWXAPI(this, PlatformConfig.INSTANCE.getWeixinId(), false);
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

    private void parseIntent() {
        Intent intent = getIntent();
        type = intent.getIntExtra(FLAG_TYPE, -1);
        content = intent.getSerializableExtra(Constants.KEY_CONTENT);
    }

    private boolean finishInResume = true;

    @Override
    protected void onResume() {
        super.onResume();
        // 没有安装微信 || 点击"留在微信中"之后再返回要关闭
        final boolean isInstalled = wxApi.isWXAppInstalled();
        if (!isInstalled || finishInResume) {
            int code = !isInstalled ? Constants.Code.ERROR_NOT_INSTALL : Constants.Code.SUCCESS;
            onResult(code, null);
        }
        finishInResume = true;
    }

    private void dispatchEvent() {
        switch (type) {
            case TYPE_SHARE_FRIEND: {
                if (null != content) {
                    share((ShareContent) content, SendMessageToWX.Req.WXSceneSession);
                } else {
                    onResult(Constants.Code.ERROR, null);
                }
                break;
            }
            case TYPE_SHARE_CIRCLE: {
                if (null != content) {
                    share((ShareContent) content, SendMessageToWX.Req.WXSceneTimeline);
                } else {
                    onResult(Constants.Code.ERROR, null);
                }
                break;
            }
            case TYPE_LOGIN: {
                login();
                break;
            }
            default: {
                onResult(Constants.Code.ERROR_NOT_SUPPORT, null);
                break;
            }
        }
    }

    private void share(ShareContent shareContent, final int scene) {
        ContentConverter.getWeixinContent(getResources(), shareContent, new SimpleListener<WXMediaMessage>() {
            @Override
            public void call(final WXMediaMessage wxMediaMessage) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        share(wxMediaMessage, scene);
                    }
                });
            }
        });
    }

    private void share(WXMediaMessage message, int scene) {
        if (null == message) {
            onResult(Constants.Code.ERROR, null);
            return;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = scene;
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
                        onResult(Constants.Code.ERROR, null);
                        return;
                    }
                    final String code = ((SendAuth.Resp) resp).token;
                    if (TextUtils.isEmpty(code)) {
                        onResult(Constants.Code.ERROR, null);
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            final AccountInfo accountInfo = WeixinAPI.getAccountInfo(code);
                            if (isFinishing()) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onResult(null == accountInfo ? Constants.Code.ERROR : Constants.Code.SUCCESS, accountInfo);
                                }
                            });
                        }
                    }.start();
                } else {
                    // other type
                    onResult(Constants.Code.SUCCESS, null);
                }
                break;
            }
            case BaseResp.ErrCode.ERR_USER_CANCEL: {
                onResult(Constants.Code.ERROR_CANCEL, null);
                break;
            }
            case BaseResp.ErrCode.ERR_AUTH_DENIED: {
                onResult(Constants.Code.ERROR_AUTH_DENIED, null);
                break;
            }
            default: {
                onResult(Constants.Code.ERROR, null);
                break;
            }
        }
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
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private static class WeixinAPI {

        /**
         * 通过认证Code换取token
         */
        private static final String API_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
        /**
         * 通过token换取用户信息
         */
        private static final String API_GET_USER = "https://api.weixin.qq.com/sns/userinfo";

        private static AccountInfo getAccountInfo(final String code) {
            final Map<String, Object> params = new HashMap<>(4);
            params.put("appid", PlatformConfig.INSTANCE.getWeixinId());
            params.put("secret", PlatformConfig.INSTANCE.getWeixinSecret());
            params.put("code", code);
            params.put("grant_type", "authorization_code");
            // get token info
            final String tokenInfo = HttpsUtils.get(API_GET_TOKEN, params);
            try {
                // tokenInfo:{access_token,expires_in,refresh_token,openid,scope,unionid}
                JSONObject json = new JSONObject(tokenInfo);
                final String assessToken = Tools.getJsonString(json, "access_token");
                final String openId = Tools.getJsonString(json, "openid");
                final long expiresIn = Tools.getLong(Tools.getJsonString(json, "expires_in"), 0);
                // get user info
                params.clear();
                params.put("access_token", assessToken);
                params.put("openid", openId);
                final String userInfo = HttpsUtils.get(API_GET_USER, params);
                final AccountInfo accountInfo = toAccountInfo(userInfo);
                accountInfo.token = AccessToken.createToken(openId, assessToken, expiresIn);
                return accountInfo;
            } catch (Exception e) {
                return null;
            }
        }

        private static AccountInfo toAccountInfo(String userInfo) throws Exception {
            // user:{subscribe,openid,nickname,sex,language,city,province,country,headimgurl,subscribe_time}
            JSONObject json = new JSONObject(userInfo);
            final String openId = Tools.getJsonString(json, "openid");
            if (TextUtils.isEmpty(openId)) {
                throw new FormatException(userInfo);
            }
            AccountInfo accountInfo = new AccountInfo(ChannelType.WEIXIN);
            accountInfo.id = openId;
            accountInfo.nickName = Tools.getJsonString(json, "nickname");
            accountInfo.headerImg = Tools.getJsonString(json, "headimgurl");
            accountInfo.gender = Tools.getJsonInt(json, "sex", 0);
            accountInfo.location = Tools.getJsonString(json, "city");
            accountInfo.putExtra("openId", openId);
            accountInfo.putExtra("language", Tools.getJsonString(json, "language"));
            accountInfo.putExtra("city", Tools.getJsonString(json, "city"));
            accountInfo.putExtra("province", Tools.getJsonString(json, "province"));
            accountInfo.putExtra("country", Tools.getJsonString(json, "country"));
            return accountInfo;
        }

    }

}
