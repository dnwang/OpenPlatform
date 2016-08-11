package com.iflytek.platform;

import com.iflytek.platform.callbacks.OnLoginListener;
import com.iflytek.platform.callbacks.OnPayListener;
import com.iflytek.platform.callbacks.OnShareListener;
import com.iflytek.platform.entity.ShareContent;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,20:30
 * @see
 */
public enum PlatformManager {

    INSTANCE;

    PlatformManager() {

    }

    public void share(Platforms platform, ShareContent content, OnShareListener listener) {


    }

    public void login(Platforms platforms, OnLoginListener listener) {


    }

    public void pay(Platforms platforms, OnPayListener listener) {


    }

}
