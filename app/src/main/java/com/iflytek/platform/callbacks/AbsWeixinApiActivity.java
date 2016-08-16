package com.iflytek.platform.callbacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.iflytek.platform.utils.Https;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.platformtools.Log;

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
        parseIntent();
        wxApi = WXAPIFactory.createWXAPI(this, APP_ID, false);
        wxApi.handleIntent(getIntent(), this);
        dispatchEvent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        type = intent.getIntExtra(FLAG_TYPE, -1);
        content = intent.getSerializableExtra(FLAG_CONTENT);
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
        req.state = "carjob_wx_login";
        wxApi.sendReq(req);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxApi.handleIntent(intent, this);
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
                    final String token = ((SendAuth.Resp) resp).token;
                    if (TextUtils.isEmpty(token)) {
                        onResult(StateCodes.ERROR, null);
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            final String url = String.format(Locale.PRC, "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s", token, APP_SECRET);
                            final String result = Https.get(url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("--------------", result);

                                    if (TextUtils.isEmpty(result)) {
                                        onResult(StateCodes.ERROR, null);
                                    } else {
                                        Serializable content = null;
                                        onResult(StateCodes.SUCCESS, content);
                                    }
                                }
                            });
                        }
                    }.start();
                } else {
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

    private void onResult(int code, Serializable content) {
        Intent intent = getIntent();
        intent.putExtra(FLAG_CODE, code);
        if (null != content) {
            intent.putExtra(FLAG_CONTENT, content);
        }
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private AccountInfo toAccountInfo(SendAuth.Resp resp) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.id = resp.userName;
        accountInfo.nikename = resp.userName;
        accountInfo.token = resp.token;
        accountInfo.expireDate = resp.expireDate;
        return accountInfo;
    }

}
