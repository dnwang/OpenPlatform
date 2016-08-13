package com.iflytek.platform;

import android.content.Context;

import com.iflytek.platform.callbacks.Callback;
import com.iflytek.platform.callbacks.Callback2;
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

    void share(Context context, ShareContent content, Callback callback);

    void login(Context context, Callback2<AccountInfo> callback);

    void getFriends(Context context, Callback2<List<AccountInfo>> Callback);

}
