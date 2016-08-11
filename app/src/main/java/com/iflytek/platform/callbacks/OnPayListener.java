package com.iflytek.platform.callbacks;

import com.iflytek.platform.Platforms;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,23:12
 * @see
 */
public interface OnPayListener {

    void onComplete(Platforms platform, boolean isSuccess, String msg, int code);

}
