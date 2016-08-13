package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:55
 * @see
 */
public final class Weixin extends Platform implements Socialize {

    public static final String APP_ID = "wxf04bacbcee9b5cc7";
    private static final String APP_SECRET = "9299bfd1ec0104a4cad2faa23010a580";

    private IWXAPI wxApi;

    private Callback callback;

    private final IWXAPIEventHandler handler = new IWXAPIEventHandler() {
        @Override
        public void onReq(BaseReq baseReq) {

        }

        @Override
        public void onResp(BaseResp resp) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (null != callback) {
                        callback.call(true, "", 1);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    if (null != callback) {
                        callback.call(false, "cancel", 0);
                    }
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    if (null != callback) {
                        callback.call(false, "auth denied", -1);
                    }
                    break;
                default:
                    if (null != callback) {
                        callback.call(false, "unknown", -1);
                    }
                    break;
            }
            callback = null;
        }
    };

    public Weixin(Context context) {
        super(context);
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID);
        wxApi.registerApp(APP_ID);
    }

    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        if (null != wxApi) {
            wxApi.handleIntent(activity.getIntent(), handler);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (null != wxApi) {
            wxApi.handleIntent(intent, handler);
        }
    }

    @Override
    public void share(Context context, ShareContent content, Callback callback) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = content.content;

        WXMediaMessage message = new WXMediaMessage(textObject);
        message.description = content.title;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
//        req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        wxApi.sendReq(req);
        this.callback = callback;
    }

    @Override
    public void login(Context context, Callback2<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Context context, Callback2<List<AccountInfo>> Callback) {

    }
}
