package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;
import org.pinwheel.platformsdk.Channel;
import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.AccessToken;
import org.pinwheel.platformsdk.entity.AccountInfo;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;

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
final class TencentQQ extends Channel implements Socialize {

    private Tencent shareApi;
    private IUiListener shareCallback;
    private IUiListener loginCallback;

    public TencentQQ(Context context) {
        super(context);
        shareApi = Tencent.createInstance(PlatformConfig.INSTANCE.getTencentId(), context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN
                || requestCode == com.tencent.connect.common.Constants.REQUEST_APPBAR) {
            if (null != loginCallback) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginCallback);
            }
            loginCallback = null;
        } else if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE) {
            if (null != shareCallback) {
                Tencent.onActivityResultData(requestCode, resultCode, data, shareCallback);
            }
            shareCallback = null;
        }
    }

    @Override
    public void share(ShareContent content, final Callback<Object> callback) {
        if (null == content) {
            return;
        }
        shareCallback = new UIListenerWrapper<>(ChannelType.QQ, callback);
        shareApi.shareToQQ((Activity) getContext(), ContentConverter.getQQContent(content), shareCallback);
    }

    @Override
    public void login(final Callback<AccountInfo> callback) {
        loginCallback = new UIListenerWrapper<AccountInfo>(ChannelType.QQ, callback) {
            @Override
            public void onComplete(Object obj) {
                if (null == getCallback()) {
                    return;
                }
                if (null == obj) {
                    getCallback().call(ChannelType.QQ, null, null, Constants.Code.ERROR);
                    return;
                }
                JSONObject json = (JSONObject) obj;
                final String token = Tools.getJsonString(json, com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                final String expires = Tools.getJsonString(json, com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                final String openId = Tools.getJsonString(json, com.tencent.connect.common.Constants.PARAM_OPEN_ID);

                final QQToken qqToken = new QQToken(PlatformConfig.INSTANCE.getTencentId());
                qqToken.setAuthSource(QQToken.AUTH_QQ);
                qqToken.setAccessToken(token, expires);
                qqToken.setOpenId(openId);

                UserInfo userInfo = new UserInfo(getContext(), qqToken);
                userInfo.getUserInfo(new UIListenerWrapper<AccountInfo>(ChannelType.QQ, getCallback()) {
                    @Override
                    public void onComplete(Object obj) {
                        try {
                            final AccountInfo accountInfo = toAccountInfo(openId, String.valueOf(obj));
                            accountInfo.token = AccessToken.createToken(qqToken);
                            getCallback().call(ChannelType.QQ, accountInfo, null, Constants.Code.SUCCESS);
                        } catch (Exception e) {
                            getCallback().call(ChannelType.QQ, null, e.getMessage(), Constants.Code.ERROR);
                        }
                    }
                });
            }
        };
        shareApi.login((Activity) getContext(), "all", loginCallback);
    }

    @Override
    public void getFriends(Callback<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(ChannelType.QQ, null, null, Constants.Code.ERROR_NOT_SUPPORT);
        }
    }

    private AccountInfo toAccountInfo(String openId, String userInfo) throws Exception {
        // user:{ret,msg,is_lost,nickname,gender,province,city,figureurl,figureurl_1,figureurl_2,figureurl_qq_1,figureurl_qq_2}
        JSONObject json = new JSONObject(userInfo);
        final String nickName = Tools.getJsonString(json, "nickname");
        if (TextUtils.isEmpty(nickName)) {
            throw new FormatException(userInfo);
        }
        AccountInfo accountInfo = new AccountInfo(ChannelType.QQ);
        accountInfo.id = openId;
        accountInfo.nickName = nickName;
        accountInfo.headerImg = Tools.getJsonString(json, "figureurl");
        final String gender = Tools.getJsonString(json, "gender");
        accountInfo.gender = "男".equals(gender) ? 1 : ("女".equals(gender) ? 2 : 0);
        accountInfo.location = Tools.getJsonString(json, "city");
        accountInfo.putExtra("figureurl_1", Tools.getJsonString(json, "figureurl_1"));
        accountInfo.putExtra("figureurl_2", Tools.getJsonString(json, "figureurl_2"));
        accountInfo.putExtra("province", Tools.getJsonString(json, "province"));
        accountInfo.putExtra("city", Tools.getJsonString(json, "city"));
        accountInfo.putExtra("description", Tools.getJsonString(json, "msg"));
        return accountInfo;
    }

    static class UIListenerWrapper<T> implements IUiListener {

        private Callback<T> callback;
        private ChannelType type;

        UIListenerWrapper(ChannelType channelType, Callback<T> callback) {
            this.callback = callback;
            this.type = channelType;
        }

        protected Callback<T> getCallback() {
            return callback;
        }

        @Override
        public void onComplete(Object o) {
            if (null == callback) {
                return;
            }
            callback.call(type, null, null, Constants.Code.SUCCESS);
        }

        @Override
        public void onCancel() {
            if (null == callback) {
                return;
            }
            callback.call(type, null, null, Constants.Code.ERROR_CANCEL);
        }

        @Override
        public void onError(UiError uiError) {
            if (null == callback) {
                return;
            }
            callback.call(type, null, String.valueOf(uiError.errorCode), Constants.Code.ERROR);
        }
    }

}
