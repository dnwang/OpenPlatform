package org.pinwheel.platformsdk.channel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.HttpsUtils;
import org.pinwheel.platformsdk.utils.Tools;

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

    static String getSimpleContent(ShareContent shareContent) {
        final String title = (null != shareContent.title) ? shareContent.title : "";
        final String content = (null != shareContent.content) ? shareContent.content : "";
        final String linkUrl = (null != shareContent.linkUrl) ? shareContent.linkUrl : "";
        final String mediaUrl = (null != shareContent.mediaUrl) ? shareContent.mediaUrl : "";
        return title + "\n" + content + "\n" + linkUrl + "\n" + mediaUrl;
    }

    /**
     * 现在微博分享修改为openapi
     */
    @Deprecated
    static void getWeiboContent(Resources res, ShareContent content, final SimpleListener<WeiboMultiMessage> listener) {
        final WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.title = content.title;
        textObject.text = content.content;
        textObject.actionUrl = TextUtils.isEmpty(content.linkUrl) ? content.mediaUrl : content.linkUrl;
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

    static void getWeixinContent(Resources res, ShareContent content, final SimpleListener<WXMediaMessage> listener) {
        final WXMediaMessage.IMediaObject mediaObj;
        if (TextUtils.isEmpty(content.mediaUrl)) {
            WXWebpageObject webObj = new WXWebpageObject();
            webObj.webpageUrl = content.linkUrl;
            mediaObj = webObj;
        } else {
            WXVideoObject videoObj = new WXVideoObject();
            videoObj.videoUrl = content.mediaUrl;
            mediaObj = videoObj;
        }

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

    static Bundle getQQContent(ShareContent content) {
        final Bundle params = new Bundle();
        final String url = TextUtils.isEmpty(content.linkUrl) ? content.mediaUrl : content.linkUrl;
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, content.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content.content);
        if (null != content.image && content.image instanceof String) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, String.valueOf(content.image));
        }
        return params;
    }

    static Bundle getQZoneContent(ShareContent content) {
        final Bundle params = new Bundle();
        final String url = TextUtils.isEmpty(content.linkUrl) ? content.mediaUrl : content.linkUrl;
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

    static void getBitmap(final Resources resources, final Object image, final SimpleListener<Bitmap> listener) {
        if (null != image) {
            new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    if (image instanceof String) {
                        bitmap = HttpsUtils.getBitmap((String) image);
                    } else if (image instanceof byte[]) {
                        byte[] data = (byte[]) image;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } else if (image instanceof Integer) {
                        bitmap = BitmapFactory.decodeResource(resources, (Integer) image);
                    }
                    listener.call(Tools.scaleBitmap(bitmap, 100));// 微信分享图片不能过大,限制100KB
                }
            }.start();
        } else {
            listener.call(null);
        }
    }

}
