package org.pinwheel.platformsdk.channel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import org.pinwheel.platformsdk.callbacks.SimpleListener;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.HttpsUtils;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXVideoObject;

import java.util.ArrayList;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/9/5,11:29
 * @see
 */
final class ContentConverter {

    private ContentConverter() {
        throw new AssertionError();
    }

    public static String getSimpleContent(ShareContent content) {
        return content.title + "\n" +
                content.content + "\n" +
                content.mediaUrl + "\n" +
                content.linkUrl;
    }

    public static void getWeiboContent(Resources res, ShareContent content, final SimpleListener<WeiboMultiMessage> listener) {
        final WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.title = content.title;
        textObject.text = content.content;
        textObject.actionUrl = TextUtils.isEmpty(content.mediaUrl) ? content.linkUrl : content.mediaUrl;
        final ImageObject imageObject = new ImageObject();
        weiboMultiMessage.textObject = textObject;
        weiboMultiMessage.imageObject = imageObject;
        getBitmap(res, content.image, new SimpleListener<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                imageObject.setImageObject(bitmap);
                if (null != listener) {
                    listener.call(weiboMultiMessage);
                }
            }
        });
    }

    public static void getWeixinContent(Resources res, ShareContent content, final SimpleListener<WXMediaMessage> listener) {
        final WXVideoObject mediaObj = new WXVideoObject();
        mediaObj.videoUrl = TextUtils.isEmpty(content.mediaUrl) ? content.linkUrl : content.mediaUrl;
        final WXMediaMessage message = new WXMediaMessage(mediaObj);
        message.description = content.content;
        message.title = content.title;
        getBitmap(res, content.image, new SimpleListener<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                message.setThumbImage(bitmap);
                if (null != listener) {
                    listener.call(message);
                }
            }
        });
    }

    public static Bundle getQQContent(ShareContent content) {
        final Bundle params = new Bundle();
        String url = TextUtils.isEmpty(content.mediaUrl) ? content.linkUrl : content.mediaUrl;
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content.content);
        if (null != content.image && content.image instanceof String) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, String.valueOf(content.image));
        }
        return params;
    }

    public static Bundle getQZoneContent(ShareContent content) {
        final Bundle params = new Bundle();
        String url = TextUtils.isEmpty(content.mediaUrl) ? content.linkUrl : content.mediaUrl;
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content.content);
        if (null != content.image && content.image instanceof String) {
            ArrayList<String> images = new ArrayList<>();
            images.add((String) content.image);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);
        }
        return params;
    }

    private static void getBitmap(final Resources resources, final Object image, final SimpleListener<Bitmap> listener) {
        if (null != image) {
            new Thread() {
                @Override
                public void run() {
                    if (image instanceof String) {
                        listener.call(HttpsUtils.getBitmap((String) image));
                    } else if (image instanceof byte[]) {
                        byte[] data = (byte[]) image;
                        listener.call(BitmapFactory.decodeByteArray(data, 0, data.length));
                    } else if (image instanceof Integer) {
                        listener.call(BitmapFactory.decodeResource(resources, (Integer) image));
                    }
                }
            }.start();
        } else {
            listener.call(null);
        }
    }

}