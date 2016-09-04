package com.iflytek.platform.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:29
 * @see
 */
public class ShareContent implements Serializable {

    public String title;
    public String content;
    public Object image;
    public String targetUrl;

    public String mediaUrl;

    private ShareContent(String title, String content, Object image, String targetUrl, String mediaUrl) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.targetUrl = targetUrl;
        this.mediaUrl = mediaUrl;
    }

    public String getSimpleTxtContent() {
        return title + "\n" +
                content + "\n" +
                ((null != image && image instanceof String) ? image : "") +
                targetUrl;
    }

    public WeiboMultiMessage getWeiboContent(Bitmap bitmap) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        // text obj
        TextObject textObject = new TextObject();
        textObject.title = title;
        textObject.text = content;
        // image obj
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        //
        weiboMultiMessage.textObject = textObject;
        weiboMultiMessage.imageObject = imageObject;
        return weiboMultiMessage;
    }

    public final static class Builder {

        private String title;
        private String content;
        private Object image;
        private String targetUrl;

        private String mediaUrl;

        public Builder() {
            title = "";
            content = "";
            image = null;
            targetUrl = "";
            mediaUrl = "";
        }

        public Builder(ShareContent shareContent) {
            this();
            if (null != shareContent) {
                title = shareContent.title;
                content = shareContent.content;
                image = shareContent.image;
                targetUrl = shareContent.targetUrl;
                mediaUrl = shareContent.mediaUrl;
            }
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder image(Bitmap bitmap) {
            this.image = bitmap;
            return this;
        }

        public Builder image(String url) {
            this.image = url;
            return this;
        }

        public Builder targetUrl(String url) {
            this.targetUrl = url;
            return this;
        }

        public Builder mediaUrl(String url) {
            this.mediaUrl = url;
            return this;
        }

        public ShareContent create() {
            return new ShareContent(title, content, image, targetUrl, mediaUrl);
        }

    }

}
