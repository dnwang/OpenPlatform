package com.iflytek.platform.callbacks;

import com.iflytek.platform.Platforms;
import com.iflytek.platform.entity.AccountInfo;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,23:11
 * @see
 */
public interface OnLoginListener {

    void onSuccess(Platforms platform, AccountInfo accountInfo);

    void onError(Platforms platform, Exception e);

}
