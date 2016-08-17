package com.iflytek.platform.callbacks;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/13/16,08:07
 * @see
 */
public interface Callback {

    void call(boolean isSuccess, String msg, int code);

}
