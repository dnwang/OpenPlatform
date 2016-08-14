package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
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
final class SinaWeibo extends Platform implements Socialize {

    private static String APP_KEY = "778164658";
    private static String APP_SECRET = "06552db3dc303529ba971b257379c49e";

    private static String REDIRECT_URL = "http://www.sina.com";
    private static String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    private IWeiboShareAPI shareAPI;

    private final IWeiboHandler.Response response = new IWeiboHandler.Response() {
        @Override
        public void onResponse(BaseResponse baseResponse) {
            // TODO: 8/13/16
            Log.e("--> SineWeibo", String.valueOf(baseResponse));
        }
    };

    public SinaWeibo(Context context) {
        super(context);
        shareAPI = WeiboShareSDK.createWeiboAPI(context, APP_KEY);
        shareAPI.registerApp();
    }

    @Override
    public void onCreate(Activity activity, Bundle bundle) {
        if (null != shareAPI && null != bundle) {
            shareAPI.handleWeiboResponse(activity.getIntent(), response);
        }
    }

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        if (null != shareAPI) {
            shareAPI.handleWeiboResponse(intent, response);
        }
    }

    @Override
    public void share(final Context context, final ShareContent content, final Callback callback) {
        if (!(context instanceof Activity)) {
            if (null != callback) {
                callback.call(false, "", -1);
            }
            return;
        }

        AuthInfo authInfo = new AuthInfo(context, APP_KEY, REDIRECT_URL, SCOPE);

        TextObject textObject = new TextObject();
        textObject.text = content.content;
        textObject.title = content.title;

        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = content.imageUrl;

        WeiboMultiMessage message = new WeiboMultiMessage();
        message.textObject = textObject;
        message.imageObject = imageObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = message;

        shareAPI.sendRequest((Activity) context, request, authInfo, "", new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                if (null != callback) {
                    callback.call(false, arg0.getMessage(), StateCodes.ERROR);
                }
            }

            @Override
            public void onComplete(Bundle bundle) {
                final Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
                if (null != callback) {
                    callback.call(true, null, StateCodes.SUCCESS);
                }
            }

            @Override
            public void onCancel() {
                if (null != callback) {
                    callback.call(false, null, StateCodes.ERROR_CANCEL);
                }
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
