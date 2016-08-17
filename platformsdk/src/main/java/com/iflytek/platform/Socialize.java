package com.iflytek.platform;

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
interface Socialize {

    void share(ShareContent content, Callback<Object> callback);

    void login(Callback<AccountInfo> callback);

    void getFriends(Callback<List<AccountInfo>> callback);

}
