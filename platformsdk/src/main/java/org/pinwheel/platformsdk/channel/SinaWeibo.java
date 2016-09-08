package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.AccessToken;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.HttpsUtils;
import org.pinwheel.platformsdk.utils.Tools;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
final class SinaWeibo extends Channel implements Socialize, SilentlySocialize {

    static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
            "follow_app_official_microblog,invitation_write";

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
                shareCallback.call(ChannelType.WEIBO, null, null, code);
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
        // 分享在“取消”时只能通过Activity接收消息
        SinaWeiboShareActivity.startActivity((Activity) getContext(), content);
        shareCallback = callback;
    }

    @Override
    public void login(Callback<AccountInfo> callback) {
        AuthInfo authInfo = new AuthInfo(getContext(), PlatformConfig.INSTANCE.getSinaKey(), PlatformConfig.INSTANCE.getSinaRedirectUrl(), SCOPE);
        ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListenerWrapper<AccountInfo>(callback) {
            @Override
            void onAuthComplete(final Oauth2AccessToken tokenInfo) {
                new Thread() {
                    @Override
                    public void run() {
                        String uid = null;
                        String token = null;
                        try {
                            uid = URLEncoder.encode(tokenInfo.getUid(), "UTF-8");
                            token = URLEncoder.encode(tokenInfo.getToken(), "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final AccountInfo accountInfo = SinaWeiboAPI.getAccountInfo(uid, token);
                        if (null != accountInfo) {
                            accountInfo.token = AccessToken.createToken(uid, token, tokenInfo.getExpiresTime());
                        }
                        final Activity activity = (Activity) getContext();
                        if (activity.isFinishing()) {
                            return;
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Callback<AccountInfo> c = getCallback();
                                if (null != c) {
                                    final boolean isSuccess = (null != accountInfo);
                                    c.call(ChannelType.WEIBO, accountInfo, null, isSuccess ? Constants.Code.SUCCESS : Constants.Code.ERROR);
                                }
                            }
                        });
                    }
                }.start();
            }
        });
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        AuthInfo authInfo = new AuthInfo(getContext(), PlatformConfig.INSTANCE.getSinaKey(), PlatformConfig.INSTANCE.getSinaRedirectUrl(), SCOPE);
        ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListenerWrapper<List<AccountInfo>>(callback) {
            @Override
            void onAuthComplete(final Oauth2AccessToken tokenInfo) {
                getFriends(AccessToken.createToken(tokenInfo), getCallback());
            }
        });
    }

    @Override
    public void getFriends(final AccessToken token, final Callback<List<AccountInfo>> callback) {
        if (null == token) {
            if (null != callback) {
                callback.call(ChannelType.WEIBO, null, null, Constants.Code.ERROR_AUTH_DENIED);
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                final List<AccountInfo> accountInfoList = SinaWeiboAPI.getFriendsList(token.getUid(), token.getToken());
                final Activity activity = (Activity) getContext();
                if (activity.isFinishing()) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != callback) {
                            final boolean isSuccess = (null != accountInfoList);
                            callback.call(ChannelType.WEIBO, accountInfoList, null, isSuccess ? Constants.Code.SUCCESS : Constants.Code.ERROR);
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void share(final AccessToken token, final ShareContent content, final Callback<Object> callback) {
        if (null == token) {
            if (null != callback) {
                callback.call(ChannelType.WEIBO, null, null, Constants.Code.ERROR_AUTH_DENIED);
            }
            return;
        }
        if (null == content) {
            if (null != callback) {
                callback.call(ChannelType.WEIBO, null, null, Constants.Code.ERROR);
            }
        }
        new Thread() {
            @Override
            public void run() {
                final boolean isSuccess = SinaWeiboAPI.shareTxt(content, token.getToken());
                final Activity activity = (Activity) getContext();
                if (activity.isFinishing()) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != callback) {
                            callback.call(ChannelType.WEIBO, null, null, isSuccess ? Constants.Code.SUCCESS : Constants.Code.ERROR);
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 微博API
     */
    private static class SinaWeiboAPI {

        /**
         * 分享纯文本
         */
        private static final String API_SHARE_TXT = "https://api.weibo.com/2/statuses/update.json";
        /**
         * 分享带图片
         * 接口未调试成功
         */
        private static final String API_SHARE_IMAGE = "https://upload.api.weibo.com/2/statuses/upload.json";
        /**
         * 认证token获取用户信息
         */
        private static final String API_SHOW_USER = "https://api.weibo.com/2/users/show.json";
        /**
         * 获取好友关注列表,单页最大count=200
         */
        private static final String API_GET_FRIENDS = "https://api.weibo.com/2/friendships/friends.json";
        private static final int PAGE_SIZE = 200;

        private static boolean shareTxt(ShareContent shareContent, String token) {
            if (null == shareContent || TextUtils.isEmpty(token)) {
                return false;
            }
            try {
                String content = URLEncoder.encode(ContentConverter.getSimpleContent(shareContent), "UTF-8");
                Map<String, Object> params = new HashMap<>(2);
                params.put("access_token", token);
                params.put("status", content);
                final String result = HttpsUtils.post(API_SHARE_TXT, null, params);
                return null != result && result.contains("created_at");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 获取用户信息
         */
        private static AccountInfo getAccountInfo(String uid, String token) {
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
                return null;
            }
            try {
                Map<String, Object> params = new HashMap<>(2);
                params.put("access_token", token);
                params.put("uid", uid);
                // {"name":"test","id":"test",...}
                String result = HttpsUtils.get(API_SHOW_USER, params);
                return toAccountInfo(new JSONObject(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 获取用户的关注列表
         * <p>
         * 只返回同样授权本应用的用户，非授权用户将不返回；
         * 例如一次调用count是50，但其中授权本应用的用户只有10条，则实际只返回10条；
         * 使用官方移动SDK调用，多返回30%的非同样授权本应用的用户，总上限为500；
         */
        private static List<AccountInfo> getFriendsList(String uid, String token) {
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
                return null;
            }
            try {
                Map<String, Object> params = new HashMap<>(4);
                params.put("access_token", token);
                params.put("uid", uid);
                params.put("count", PAGE_SIZE);
                params.put("cursor", 0);
                // {"users":[],"next_cursor":0,"previous_cursor":0,"total_number":1}
                String result = HttpsUtils.get(API_GET_FRIENDS, params);
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("users");
                final int size = jsonArray.length();
                List<AccountInfo> accountInfoList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    AccountInfo accountInfo = toAccountInfo(jsonArray.getJSONObject(i));
                    accountInfoList.add(accountInfo);
                }
                return accountInfoList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private static AccountInfo toAccountInfo(JSONObject json) throws Exception {
            final String uid = Tools.getJsonString(json, "id");
            if (TextUtils.isEmpty(uid)) {
                throw new FormatException("account json is not contains user id.");
            }
            AccountInfo accountInfo = new AccountInfo(ChannelType.WEIBO);
            accountInfo.id = uid;
            accountInfo.nickName = Tools.getJsonString(json, "name");
            accountInfo.headerImg = Tools.getJsonString(json, "profile_image_url");
            final String gender = Tools.getJsonString(json, "gender").toLowerCase();
            accountInfo.gender = "m".equals(gender) ? 1 : ("w".equals(gender) ? 2 : 0);
            accountInfo.location = Tools.getJsonString(json, "location");
            accountInfo.putExtra("avatar_large", Tools.getJsonString(json, "avatar_large"));
            accountInfo.putExtra("avatar_hd", Tools.getJsonString(json, "avatar_hd"));
            accountInfo.putExtra("language", Tools.getJsonString(json, "lang"));
            accountInfo.putExtra("location", Tools.getJsonString(json, "location"));
            accountInfo.putExtra("description", Tools.getJsonString(json, "description"));
            return accountInfo;
        }

    }

    private static abstract class WeiboAuthListenerWrapper<T> implements WeiboAuthListener {

        private Callback<T> callback;

        private WeiboAuthListenerWrapper(Callback<T> callback) {
            this.callback = callback;
        }

        public Callback<T> getCallback() {
            return callback;
        }

        abstract void onAuthComplete(Oauth2AccessToken tokenInfo);

        @Override
        public void onComplete(Bundle bundle) {
            if (null == callback) {
                return;
            }
            final Oauth2AccessToken tokenInfo = Oauth2AccessToken.parseAccessToken(bundle);
            if (null == tokenInfo || !tokenInfo.isSessionValid()) {
                callback.call(ChannelType.WEIBO, null, null, Constants.Code.ERROR);
                return;
            }
            onAuthComplete(tokenInfo);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (null != callback) {
                callback.call(ChannelType.WEIBO, null, e.getMessage(), Constants.Code.ERROR);
            }
        }

        @Override
        public void onCancel() {
            if (null != callback) {
                callback.call(ChannelType.WEIBO, null, null, Constants.Code.ERROR_CANCEL);
            }
        }
    }

}
