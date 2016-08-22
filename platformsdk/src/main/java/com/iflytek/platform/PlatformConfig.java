package com.iflytek.platform;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 2016/8/18,10:43
 * @see
 */
public final class PlatformConfig {

    static String WEIXIN_ID;
    static String WEIXIN_SECRET;

    static String TENCENT_ID;
    static String TENCENT_KEY;

    static String SINA_KEY;
    static String SINA_SECRET;

    static String TAOBAO_KEY;
    static String TAOBAO_SECRET;

    public static void setWeixin(String id, String secret) {
        WEIXIN_ID = id;
        WEIXIN_SECRET = secret;
    }

    public static void setTencent(String id, String key) {
        TENCENT_ID = id;
        TENCENT_KEY = key;
    }

    public static void setSina(String key, String secret) {
        SINA_KEY = key;
        SINA_SECRET = secret;
    }

    public static void setTaobao(String key, String secret) {
        TAOBAO_KEY = key;
        TAOBAO_SECRET = secret;
    }

    private PlatformConfig() {
        throw new AssertionError();
    }

}
