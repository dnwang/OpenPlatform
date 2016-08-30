package com.iflytek.platform.entity;

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
    public String imageUrl;
    public String targetUrl;

    private ShareContent() {

    }

    public String getAllContent() {
        return title + "\n" +
                content + "\n" +
                imageUrl + "\n" +
                targetUrl;
    }

    public final static class Builder {

        private ShareContent shareContent;

        public Builder() {
            shareContent = new ShareContent();
        }

        public Builder(ShareContent content) {
            this();
            if (null != content) {
                shareContent.title = content.title;
                shareContent.content = content.content;
                shareContent.imageUrl = content.imageUrl;
                shareContent.targetUrl = content.targetUrl;
            }
        }

        public Builder title(String title) {
            shareContent.title = title;
            return this;
        }

        public Builder content(String content) {
            shareContent.content = content;
            return this;
        }

        public Builder imageUrl(String url) {
            shareContent.imageUrl = url;
            return this;
        }

        public Builder targetUrl(String url) {
            shareContent.targetUrl = url;
            return this;
        }

        public ShareContent create() {
            return shareContent;
        }

    }

}
