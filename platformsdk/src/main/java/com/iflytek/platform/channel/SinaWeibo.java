package com.iflytek.platform.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.platform.Channel;
import com.iflytek.platform.PlatformConfig;
import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.Constants;
import com.iflytek.platform.entity.ShareContent;
import com.iflytek.platform.utils.HttpsUtils;
import com.iflytek.platform.utils.Tools;
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
final class SinaWeibo extends Channel implements Socialize {

    // 由于后端没有配置，始终出现21322，以下URL摘自友盟新浪分享，可用作默认值
    static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
            "follow_app_official_microblog,invitation_write";

    /**
     * 认证token获取用户信息
     */
    private static final String API_SHOW_USER = "https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s";

    private SsoHandler ssoHandler;

    private Callback<Object> shareCallback;

    public SinaWeibo(Context context) {
        super(context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // 分享回调
        if (SinaWeiboShareActivity.REQ_SINA_WEIBO == requestCode && Activity.RESULT_OK == resultCode) {
            final int code = data.getIntExtra(Constants.KEY_CODE, -1);
            final Object obj = data.getSerializableExtra(Constants.KEY_CONTENT);
            if (null != shareCallback) {
                shareCallback.call(null, null, code);
            }
        }
        shareCallback = null;
        // 登录回调
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void share(final ShareContent content, final Callback<Object> callback) {
        if (null == content) {
            return;
        }
        SinaWeiboShareActivity.startActivity((Activity) getContext(), content);
        shareCallback = callback;
    }

    @Override
    public void login(final Callback<AccountInfo> callback) {
        AuthInfo authInfo = new AuthInfo(getContext(), PlatformConfig.INSTANCE.getSinaKey(), REDIRECT_URL, SCOPE);
        ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                if (null == callback) {
                    return;
                }
                final Oauth2AccessToken tokenInfo = Oauth2AccessToken.parseAccessToken(bundle);
                if (null == tokenInfo || !tokenInfo.isSessionValid()) {
                    callback.call(null, null, Constants.Code.ERROR);
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
                                callback.call(accountInfo, null, isSuccess ? Constants.Code.SUCCESS : Constants.Code.ERROR);
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (null != callback) {
                    callback.call(null, e.getMessage(), Constants.Code.ERROR);
                }
            }

            @Override
            public void onCancel() {
                if (null != callback) {
                    callback.call(null, null, Constants.Code.ERROR_CANCEL);
                }
            }
        });
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        // TODO: 2016/8/16
        if (null != callback) {
            callback.call(null, null, Constants.Code.ERROR_NOT_SUPPORT);
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
        accountInfo.id = uid;
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
