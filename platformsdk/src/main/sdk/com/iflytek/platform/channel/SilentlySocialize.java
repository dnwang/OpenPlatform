package com.iflytek.platform.channel;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.entity.AccessToken;
import com.iflytek.platform.entity.AccountInfo;
import com.iflytek.platform.entity.ShareContent;

import java.util.List;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/30/16,23:06
 * @see
 */
public interface SilentlySocialize {

    /**
     * 无须登录认证，使用已有认证信息直接获取朋友列表
     */
    void getFriends(AccessToken token, Callback<List<AccountInfo>> callback);

    /**
     * 无须登录认证，使用已有认证信息直接分享
     */
    void share(AccessToken token, ShareContent content, Callback<Object> callback);

}
