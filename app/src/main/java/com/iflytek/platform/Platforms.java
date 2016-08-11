package com.iflytek.platform;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/11/16,22:06
 * @see
 */
public enum Platforms {

    WEIBO(SinaWeibo.class),
    ALIPAY(AliPay.class);

    private Class<? extends Platform> cls;

    Platforms(Class<? extends Platform> cls) {


    }

}
