package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.sina.weibo.sdk.auth.sso.SsoHandler;
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

    private static String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    private static String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    private IWeiboShareAPI shareAPI;

    private AuthInfo authInfo;
    private SsoHandler ssoHandler;

    private final IWeiboHandler.Response response = new IWeiboHandler.Response() {
        @Override
        public void onResponse(BaseResponse baseResponse) {
            // TODO: 8/13/16
            Log.e("--> SineWeibo", String.valueOf(baseResponse));
        }
    };

    public SinaWeibo(Context context) {
        super(context);
        authInfo = new AuthInfo(context, APP_KEY, REDIRECT_URL, SCOPE);
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
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void share(final ShareContent content, final Callback callback) {
        if (null == content) {
            return;
        }

        AuthInfo authInfo = new AuthInfo(getContext(), APP_KEY, REDIRECT_URL, SCOPE);

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

        shareAPI.sendRequest((Activity) getContext(), request, authInfo, "", new WeiboAuthListener() {

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
    public void login(final Callback2<AccountInfo> callback) {
        // TODO: 2016/8/16  code:21322 后端未配置REDIRECT_URL
        ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Toast.makeText(getContext(), "onComplete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(getContext(), "onWeiboException", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "onCancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {
        // TODO: 2016/8/16
    }
}
