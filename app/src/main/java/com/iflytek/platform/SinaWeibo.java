package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.PayInfo;
import com.iflytek.platform.entity.ShareContent;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

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
final class SinaWeibo extends Platform {

    private static String APP_KEY = "778164658";
    private static String APP_SECRET = "06552db3dc303529ba971b257379c49e";

    private static String REDIRECT_URL = "http://www.sina.com";
    private static String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    private IWeiboShareAPI shareAPI;

    public SinaWeibo(Context context) {
        super(context);
        shareAPI = WeiboShareSDK.createWeiboAPI(context, APP_KEY);
        shareAPI.registerApp();
    }

    @Override
    public void pay(Context context, PayInfo payInfo, Callback callback) {

    }

    @Override
    public void share(final Context context, final ShareContent content, Callback callback) {
        if (!(context instanceof Activity)) {
            if (null != callback) {
                callback.call(false, "", -1);
            }
            return;
        }

        AuthInfo authInfo = new AuthInfo(context, APP_KEY, REDIRECT_URL, SCOPE);

        TextObject textObject = new TextObject();
        textObject.text = "share sdk";

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = textObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        final String token = "";

        shareAPI.sendRequest((Activity) context, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                Toast.makeText(context, arg0.getMessage(), 0).show();
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                Toast.makeText(context, "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "onCancel", 0).show();
            }
        });

    }

    @Override
    public void login(Context context, Callback2<AccountInfo> callback) {

    }

    @Override
    public void getFriends(Context context, Callback2<List<AccountInfo>> callback) {

    }
}
