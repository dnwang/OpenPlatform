package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.callbacks.SimpleListener;
import org.pinwheel.platformsdk.entity.AccessToken;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;

import java.net.URLEncoder;
import java.util.ArrayList;
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
final class SinaWeibo extends Channel implements Socialize, SilentlySocialize {

    /**
     * 认证token获取用户信息
     */
    private static final String API_SHOW_USER = "https://api.weibo.com/2/users/show.json";
    /**
     * 获取用户的关注列表
     * <p>
     * 只返回同样授权本应用的用户，非授权用户将不返回；
     * 例如一次调用count是50，但其中授权本应用的用户只有10条，则实际只返回10条；
     * 使用官方移动SDK调用，多返回30%的非同样授权本应用的用户，总上限为500；
     */
    private static final String API_GET_FRIENDS = "https://api.weibo.com/2/friendships/friends.json";

    private static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
            "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
            "follow_app_official_microblog,invitation_write";

    public SinaWeibo(Context context) {
        super(context);
    }

    private void authUser(final SimpleListener<Oauth2AccessToken> listener) {
        AuthInfo authInfo = new AuthInfo(getContext(), PlatformConfig.INSTANCE.getSinaKey(), PlatformConfig.INSTANCE.getSinaRedirectUrl(), SCOPE);
        SsoHandler ssoHandler = new SsoHandler((Activity) getContext(), authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                if (null != listener) {
                    final Oauth2AccessToken tokenInfo = Oauth2AccessToken.parseAccessToken(bundle);
                    if (null == tokenInfo || !tokenInfo.isSessionValid()) {
                        listener.call(null);
                    } else {
                        listener.call(tokenInfo);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (null != listener) {
                    listener.call(null);
                }
            }

            @Override
            public void onCancel() {
                if (null != listener) {
                    listener.call(null);
                }
            }
        });
    }

    @Override
    public void share(ShareContent content, final Callback<Object> callback) {
        if (null == content) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR);
            return;
        }
        authUser(new SimpleListener<Oauth2AccessToken>() {
            @Override
            public void call(Oauth2AccessToken accessToken) {
                // TODO: 2016/10/9
                dispatchCallback(callback, null, null, Constants.Code.ERROR_NOT_SUPPORT);
            }
        });
    }

    @Override
    public void login(final Callback<AccountInfo> callback) {
        authUser(new SimpleListener<Oauth2AccessToken>() {
            @Override
            public void call(final Oauth2AccessToken accessToken) {
                if (null == accessToken) {
                    dispatchCallback(callback, null, null, Constants.Code.ERROR_AUTH_DENIED);
                    return;
                }
                final String uid, token;
                try {
                    uid = URLEncoder.encode(accessToken.getUid(), "UTF-8");
                    token = URLEncoder.encode(accessToken.getToken(), "UTF-8");
                } catch (Exception e) {
                    dispatchCallback(callback, null, e.getMessage(), Constants.Code.ERROR);
                    return;
                }
                // weibo open api
                RequestListener listener = new RequestListenerWrapper<AccountInfo>(callback) {
                    @Override
                    public void onComplete(String response) {
                        try {
                            final AccountInfo accountInfo = toAccountInfo(response);
                            accountInfo.token = AccessToken.createToken(uid, token, accessToken.getExpiresTime());
                            dispatchCallback(getCallback(), accountInfo, null, Constants.Code.SUCCESS);
                        } catch (Exception e) {
                            dispatchCallback(getCallback(), null, e.getMessage(), Constants.Code.ERROR);
                        }
                    }
                };
                WeiboParameters parameters = new WeiboParameters(PlatformConfig.INSTANCE.getSinaKey());
                parameters.put("access_token", token);
                parameters.put("uid", uid);
                new AsyncWeiboRunner(getContext()).requestAsync(API_SHOW_USER, parameters, "GET", listener);
            }
        });
    }

    @Override
    public void getFriends(final Callback<List<AccountInfo>> callback) {
        authUser(new SimpleListener<Oauth2AccessToken>() {
            @Override
            public void call(Oauth2AccessToken accessToken) {
                if (null == accessToken) {
                    dispatchCallback(callback, null, null, Constants.Code.ERROR_AUTH_DENIED);
                    return;
                }
                getFriends(AccessToken.createToken(accessToken), callback);
            }
        });
    }

    @Override
    public void getFriends(AccessToken token, Callback<List<AccountInfo>> callback) {
        if (null == token) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR_AUTH_DENIED);
            return;
        }
        // weibo open api
        RequestListener listener = new RequestListenerWrapper<List<AccountInfo>>(callback) {
            @Override
            public void onComplete(String response) {
                try {
                    // {"users":[],"next_cursor":0,"previous_cursor":0,"total_number":1}
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray("users");
                    final int size = jsonArray.length();
                    List<AccountInfo> accountInfoList = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        AccountInfo accountInfo = toAccountInfo(jsonArray.getJSONObject(i));
                        accountInfoList.add(accountInfo);
                    }
                    dispatchCallback(getCallback(), accountInfoList, null, Constants.Code.SUCCESS);
                } catch (Exception e) {
                    dispatchCallback(getCallback(), null, e.getMessage(), Constants.Code.ERROR);
                }
            }
        };
        WeiboParameters parameters = new WeiboParameters(PlatformConfig.INSTANCE.getSinaKey());
        parameters.put("access_token", token.getToken());
        parameters.put("uid", token.getUid());
        parameters.put("count", 200);
        parameters.put("cursor", 0);
        new AsyncWeiboRunner(getContext()).requestAsync(API_GET_FRIENDS, parameters, "GET", listener);
    }

    @Override
    public void share(AccessToken token, final ShareContent content, final Callback<Object> callback) {
        if (null == token) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR_AUTH_DENIED);
            return;
        }
        if (null == content) {
            dispatchCallback(callback, null, null, Constants.Code.ERROR);
            return;
        }
        // request weibo open api
        Oauth2AccessToken accessToken = new Oauth2AccessToken(token.getToken(), null);
        StatusesAPI api = new StatusesAPI(getContext(), PlatformConfig.INSTANCE.getSinaKey(), accessToken);
        RequestListener requestListener = new RequestListenerWrapper<Object>(callback) {
            @Override
            public void onComplete(String response) {
                final boolean isSuccess = !isOpenApiError(response);
                String err = isSuccess ? null : response;
                int code = isSuccess ? Constants.Code.SUCCESS : Constants.Code.ERROR;
                dispatchCallback(getCallback(), null, err, code);
            }
        };
        if (null != content.image) {
            byte[] data = (byte[]) content.image;
            api.upload(content.content, BitmapFactory.decodeByteArray(data, 0, data.length), null, null, requestListener);
        } else {
            api.update(content.content, null, null, requestListener);
        }
    }

    private <T> void dispatchCallback(Callback<T> callback, T obj, String msg, int code) {
        if (null != callback) {
            callback.call(ChannelType.WEIBO, obj, msg, code);
        }
    }

    private boolean isOpenApiError(String response) {
        return TextUtils.isEmpty(response) || response.contains("error_code");
    }

    private AccountInfo toAccountInfo(String userInfo) throws Exception {
        return toAccountInfo(new JSONObject(userInfo));
    }

    private AccountInfo toAccountInfo(JSONObject json) throws Exception {
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

    private static abstract class RequestListenerWrapper<T> implements RequestListener {

        private Callback<T> callback;

        private RequestListenerWrapper(Callback<T> callback) {
            this.callback = callback;
        }

        protected Callback<T> getCallback() {
            return callback;
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (null != callback) {
                String err = (null == e) ? null : e.getMessage();
                callback.call(ChannelType.WEIBO, null, err, Constants.Code.ERROR);
            }
        }
    }

}
