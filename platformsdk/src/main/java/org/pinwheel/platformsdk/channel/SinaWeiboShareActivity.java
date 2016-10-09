package org.pinwheel.platformsdk.channel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseRequest;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;

import org.pinwheel.platformsdk.PlatformConfig;
import org.pinwheel.platformsdk.callbacks.SimpleListener;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.ShareContent;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 * 微博新版分享,"分享成功"时可以正常回调,但"取消分享"时只能通过activity#onNewIntent接受回调
 * 并且IWeiboHandler.Response必须从Activity实现,
 * 同时Activity注册intent-filter:com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY
 *
 * @author dnwang
 * @version 8/23/16,21:01
 * @see
 */
@Deprecated
public final class SinaWeiboShareActivity extends Activity implements IWeiboHandler.Response {

    private static final String FLAG_TYPE = "type";
    /**
     * 分享
     */
    public static final int TYPE_SHARE = 100;

    public static final int REQ_SINA_WEIBO = 0x263;

    public static void startActivity(Activity activity, Serializable content) {
        Intent intent = new Intent(activity, SinaWeiboShareActivity.class);
        intent.putExtra(FLAG_TYPE, TYPE_SHARE);//暂且固定为分享
        intent.putExtra(Constants.KEY_CONTENT, content);
        activity.startActivityForResult(intent, REQ_SINA_WEIBO);
        activity.overridePendingTransition(0, 0);
    }

    private IWeiboShareAPI shareAPI;

    private int type;
    private Serializable content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(0);

        final Intent intent = getIntent();
        type = intent.getIntExtra(FLAG_TYPE, -1);
        content = intent.getSerializableExtra(Constants.KEY_CONTENT);
        if (type < 0) {
            onResult(Constants.Code.ERROR, null);
            return;
        }
        shareAPI = WeiboShareSDK.createWeiboAPI(this, PlatformConfig.INSTANCE.getSinaKey());
        shareAPI.registerApp();
        shareAPI.handleWeiboResponse(getIntent(), this);
        dispatchEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        // 目前只有分享会走到这里
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    onResult(Constants.Code.SUCCESS, null);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    onResult(Constants.Code.ERROR_CANCEL, null);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    onResult(Constants.Code.ERROR_CANCEL, null);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // FIXME: 8/23/16  但存在微博客户端时,在登录账号或取消都会已相同的返回值走到这里,暂时无法区别
        onResult(Constants.Code.ERROR_CANCEL, null);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void onResult(int code, Serializable content) {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_CODE, code);
        if (null != content) {
            intent.putExtra(Constants.KEY_CONTENT, content);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void dispatchEvent() {
        switch (type) {
            case TYPE_SHARE: {
                if (null != content) {
                    ContentConverter.getWeiboContent(getResources(), (ShareContent) content, new SimpleListener<WeiboMultiMessage>() {
                        @Override
                        public void call(final WeiboMultiMessage weiboMultiMessage) {
                            if (isFinishing()) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    share(weiboMultiMessage);
                                }
                            });
                        }
                    });
                } else {
                    onResult(Constants.Code.ERROR, null);
                }
                break;
            }
            default: {
                onResult(Constants.Code.ERROR_NOT_SUPPORT, null);
                break;
            }
        }
    }

    private void share(WeiboMultiMessage message) {
        if (null == message) {
            onResult(Constants.Code.ERROR, null);
            return;
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = message;
        final WeiboAuthListener listener = new WeiboAuthListener() {
            @Override
            public void onWeiboException(WeiboException e) {
                onResult(Constants.Code.ERROR, null);
            }

            @Override
            public void onComplete(Bundle bundle) {
                onResult(Constants.Code.SUCCESS, null);
            }

            @Override
            public void onCancel() {
                onResult(Constants.Code.ERROR_CANCEL, null);
            }
        };
//        AuthInfo authInfo = new AuthInfo(this, PlatformConfig.INSTANCE.getSinaKey(), PlatformConfig.INSTANCE.getSinaRedirectUrl(), SinaWeibo.SCOPE);
//        shareAPI.sendRequest(this, request, authInfo, "", listener);
        try {
            //仅使用网页版分享,客户端分享暂且有问题
            Class cls = Class.forName("com.sina.weibo.sdk.api.share.WeiboShareAPIImpl");
            Method method = cls.getDeclaredMethod("startShareWeiboActivity", Activity.class, String.class, BaseRequest.class, WeiboAuthListener.class);
            method.setAccessible(true);
            method.invoke(shareAPI, this, "", request, listener);
        } catch (Exception e) {
            onResult(Constants.Code.ERROR, null);
        }
    }

}
