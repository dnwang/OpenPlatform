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
import com.iflytek.platform.utils.Tools;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

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
final class TencentQQ extends Platform implements Socialize {

    static final String APP_ID = "100526240";
    static final String APP_KEY = "20bca3e9e564042b7d1e2ec6ee261b1c";

    private Tencent shareApi;
    private IUiListener shareCallback;
    private IUiListener loginCallback;

    public TencentQQ(Context context) {
        super(context);
        shareApi = Tencent.createInstance(APP_ID, context);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            if (null != loginCallback) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginCallback);
            }
            loginCallback = null;
        } else if (requestCode == Constants.REQUEST_QQ_SHARE) {
            if (null != shareCallback) {
                Tencent.onActivityResultData(requestCode, resultCode, data, shareCallback);
            }
            shareCallback = null;
        }
    }

    @Override
    public void share(ShareContent content, final Callback callback) {
        if (null == content || TextUtils.isEmpty(content.targetUrl)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, content.targetUrl);//必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content.content);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, content.imageUrl);

        shareCallback = new SampleUIListener<>(callback);
        shareApi.shareToQQ((Activity) getContext(), params, shareCallback);
    }

    @Override
    public void login(final Callback2<AccountInfo> callback) {
        loginCallback = new SampleUIListener<Callback2<AccountInfo>>(callback) {
            @Override
            public void onComplete(Object obj) {
                if (null == getCallback()) {
                    return;
                }
                if (null == obj) {
                    getCallback().call(null, false, null, StateCodes.ERROR);
                    return;
                }
                JSONObject json = (JSONObject) obj;
                final String token = Tools.getJsonString(json, Constants.PARAM_ACCESS_TOKEN);
                String expires = Tools.getJsonString(json, Constants.PARAM_EXPIRES_IN);
                String openId = Tools.getJsonString(json, Constants.PARAM_OPEN_ID);

                QQToken qqToken = new QQToken(APP_ID);
                qqToken.setAuthSource(QQToken.AUTH_QQ);
                qqToken.setAccessToken(token, expires);
                qqToken.setOpenId(openId);

                UserInfo userInfo = new UserInfo(getContext(), qqToken);
                userInfo.getUserInfo(new SampleUIListener<Callback2<AccountInfo>>(getCallback()) {
                    @Override
                    public void onComplete(Object obj) {
                        try {
                            callback.call(toAccountInfo(String.valueOf(obj)), true, null, StateCodes.SUCCESS);
                        } catch (Exception e) {
                            callback.call(null, false, null, StateCodes.ERROR);
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        shareApi.login((Activity) getContext(), "all", loginCallback);
    }

    @Override
    public void getFriends(Callback2<List<AccountInfo>> callback) {
        if (null != callback) {
            callback.call(null, false, null, StateCodes.ERROR_NOT_SUPPORT);
        }
    }

    private AccountInfo toAccountInfo(String userInfo) throws Exception {
        // user:{ret,msg,is_lost,nickname,gender,province,city,figureurl,figureurl_1,figureurl_2,figureurl_qq_1,figureurl_qq_2}
        JSONObject json = new JSONObject(userInfo);
        final String nickName = Tools.getJsonString(json, "nickname");
        if (TextUtils.isEmpty(nickName)) {
            throw new FormatException(userInfo);
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.uid = "";// 无唯一标识信息
        accountInfo.nickName = nickName;
        accountInfo.headerImg = Tools.getJsonString(json, "figureurl");
        final String gender = Tools.getJsonString(json, "gender");
        accountInfo.gender = "男".equals(gender) ? 1 : ("女".equals(gender) ? 2 : 0);
        accountInfo.putExtra("figureurl_1", Tools.getJsonString(json, "figureurl_1"));
        accountInfo.putExtra("figureurl_2", Tools.getJsonString(json, "figureurl_2"));
        accountInfo.putExtra("province", Tools.getJsonString(json, "province"));
        accountInfo.putExtra("city", Tools.getJsonString(json, "city"));
        accountInfo.putExtra("description", Tools.getJsonString(json, "msg"));
        return accountInfo;
    }

    static class SampleUIListener<T> implements IUiListener {

        private T callback;

        SampleUIListener(T callback) {
            this.callback = callback;
        }

        protected T getCallback() {
            return callback;
        }

        @Override
        public void onComplete(Object o) {
            if (null == callback) {
                return;
            }
            if (callback instanceof Callback) {
                ((Callback) callback).call(true, null, StateCodes.SUCCESS);
            } else if (callback instanceof Callback2) {
                ((Callback2) callback).call(null, true, null, StateCodes.SUCCESS);
            }
        }

        @Override
        public void onCancel() {
            if (null == callback) {
                return;
            }
            if (callback instanceof Callback) {
                ((Callback) callback).call(false, null, StateCodes.ERROR_CANCEL);
            } else if (callback instanceof Callback2) {
                ((Callback2) callback).call(null, false, null, StateCodes.ERROR_CANCEL);
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (null == callback) {
                return;
            }
            if (callback instanceof Callback) {
                ((Callback) callback).call(false, String.valueOf(uiError.errorCode), StateCodes.ERROR);
            } else if (callback instanceof Callback2) {
                ((Callback2) callback).call(null, false, String.valueOf(uiError.errorCode), StateCodes.ERROR);
            }
        }
    }

}
