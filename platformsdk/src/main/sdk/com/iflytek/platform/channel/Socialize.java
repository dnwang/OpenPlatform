package com.iflytek.platform.channel;

import com.iflytek.platform.callbacks.Callback;
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
 * @version 8/13/16,08:06
 * @see
 */
public interface Socialize {

    /**
     * 分享
     */
    void share(ShareContent content, Callback<Object> callback);

    /**
     * 授权登录
     */
    void login(Callback<AccountInfo> callback);

    /**
     * 获取好友列表
     */
    void getFriends(Callback<List<AccountInfo>> callback);

}
