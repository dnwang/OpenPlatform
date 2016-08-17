package com.iflytek.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.entity.StateCodes;
import com.iflytek.platform.utils.HttpsUtils;
import com.iflytek.platform.utils.Tools;
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

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

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

    private static final String APP_KEY = "778164658";
    private static final String APP_SECRET = "06552db3dc303529ba971b257379c49e";

    // 由于后端没有配置，始终出现21322，以下URL摘自友盟新浪分享，可用作默认值
    private static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    private static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    /**
     * 认证token获取用户信息
     */
    private static final String API_SHOW_USER = "https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s";

    private IWeiboShareAPI shareAPI;

    private AuthInfo authInfo;
    private SsoHandler ssoHandler;

    private final IWeiboHandler.Response response = new IWeiboHandler.Response() {
        @Override
        public void onResponse(BaseResponse baseResponse) {
            // TODO: 8/13/16
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
            public void onWeiboException(WeiboException e) {
                if (null != callback) {
                    callback.call(false, e.getMessage(), StateCodes.ERROR);
                }
            }

            @Override
            public void onComplete(Bundle bundle) {
                final Oauth2AccessToken tokenInfo = Oauth2AccessToken.parseAccessToken(bundle);
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
        ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                if (null == callback) {
                    return;
                }
                final Oauth2AccessToken tokenInfo = Oauth2AccessToken.parseAccessToken(bundle);
                if (null == tokenInfo || !tokenInfo.isSessionValid()) {
                    callback.call(null, false, null, StateCodes.ERROR);
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        final AccountInfo accountInfo = getAccountInfo(tokenInfo.getUid(), tokenInfo.getToken());
                        final Activity activity = (Activity) getContext();
                        if (activity.isFinishing()) {
                            return;
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final boolean isSuccess = (null != accountInfo);
                                callback.call(accountInfo, isSuccess, null, isSuccess ? StateCodes.SUCCESS : StateCodes.ERROR);
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (null != callback) {
                    callback.call(null, false, e.getMessage(), StateCodes.ERROR);
                }
            }

            @Override
            public void onCancel() {
                if (null != callback) {
                    callback.call(null, false, null, StateCodes.ERROR_CANCEL);
                }
            }
        });
    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {
        // TODO: 2016/8/16
        if (null != callback) {
            callback.call(null, false, null, StateCodes.ERROR_NOT_SUPPORT);
        }
    }

    private AccountInfo getAccountInfo(String uid, String token) {
        try {
            uid = URLEncoder.encode(uid, "UTF-8");
            token = URLEncoder.encode(token, "UTF-8");
            final String url4User = String.format(Locale.PRC, API_SHOW_USER, token, uid);
            String result = HttpsUtils.get(url4User);
            return toAccountInfo(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private AccountInfo toAccountInfo(String userInfo) throws Exception {
        // user:{...}
        JSONObject json = new JSONObject(userInfo);
        final String uid = Tools.getJsonString(json, "id");
        if (TextUtils.isEmpty(uid)) {
            throw new FormatException(userInfo);
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.uid = uid;
        accountInfo.nickName = Tools.getJsonString(json, "name");
        accountInfo.headerImg = Tools.getJsonString(json, "profile_image_url");
        final String gender = Tools.getJsonString(json, "gender").toLowerCase();
        accountInfo.gender = "m".equals(gender) ? 1 : ("w".equals(gender) ? 2 : 0);
        accountInfo.putExtra("avatar_large", Tools.getJsonString(json, "avatar_large"));
        accountInfo.putExtra("avatar_hd", Tools.getJsonString(json, "avatar_hd"));
        accountInfo.putExtra("language", Tools.getJsonString(json, "lang"));
        accountInfo.putExtra("location", Tools.getJsonString(json, "location"));
        accountInfo.putExtra("description", Tools.getJsonString(json, "description"));
        return accountInfo;
    }

}
