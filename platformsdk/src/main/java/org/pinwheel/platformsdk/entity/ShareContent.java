package org.pinwheel.platformsdk.entity;

import android.graphics.Bitmap;

import org.pinwheel.platformsdk.utils.Tools;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

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
    public Serializable image; // byte[], int, string
    public String linkUrl;
    public String mediaUrl;

    private ShareContent(String title, String content, Serializable image, String linkUrl, String mediaUrl) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.linkUrl = linkUrl;
        this.mediaUrl = mediaUrl;
    }

    public final static class Builder {

        private String title;
        private String content;
        private Serializable image;
        private String linkUrl;
        private String mediaUrl;

        public Builder() {
            title = "";
            content = "";
            image = null;
            linkUrl = "";
            mediaUrl = "";
        }

        public Builder(ShareContent shareContent) {
            this();
            if (null != shareContent) {
                title = shareContent.title;
                content = shareContent.content;
                image = shareContent.image;
                linkUrl = shareContent.linkUrl;
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

        /**
         * Bitmap, String(url), Int(resId)
         */
        public Builder image(Object obj) {
            if (null != obj) {
                if (obj instanceof Bitmap) {
                    image((Bitmap) obj);
                } else if (obj instanceof String || obj instanceof Integer) {
                    this.image = (Serializable) obj;
                }
            } else {
                this.image = null;
            }
            return this;
        }

        private void image(Bitmap bitmap) {
            if (null != bitmap) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                this.image = outputStream.toByteArray();
                Tools.close(outputStream);
            }
        }

        public Builder linkUrl(String url) {
            this.linkUrl = url;
            return this;
        }

        public Builder mediaUrl(String url) {
            this.mediaUrl = url;
            return this;
        }

        public ShareContent create() {
            return new ShareContent(title, content, image, linkUrl, mediaUrl);
        }

    }

}
