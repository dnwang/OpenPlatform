package com.iflytek.platform;

import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:21
 * @see
 */
public interface Socialize {

    interface OnLoginListener {
        void onSuccess(AccountInfo accountInfo);

        void onError(Exception e);
    }

    interface OnShareListener {
        void onComplete(PlatformType platformType, boolean isSuccess, String msg);
    }

    void share(ShareContent content, OnShareListener listener);

    void login(OnLoginListener listener);

}
