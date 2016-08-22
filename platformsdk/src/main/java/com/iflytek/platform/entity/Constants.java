package com.iflytek.platform.entity;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/14/16,22:12
 * @see
 */
public interface Constants {

    /**
     * 平台标示
     */
    interface PlatformFlag {
        int SINA = 1;
        int WEIXIN = 1 << 1;
        int TENCENT = 1 << 2;
        int UMENG = 1 << 3;
        int TAOBAO = 1 << 4;
        int ALIPAY = 1 << 5;
    }

    interface Code {
        /**
         * 未知错误
         */
        int ERROR = 100;
        /**
         * 主动取消操作
         */
        int ERROR_CANCEL = 101;
        /**
         * 功能不支持
         */
        int ERROR_NOT_SUPPORT = 102;
        /**
         * 认证失败
         */
        int ERROR_AUTH_DENIED = 103;
        /**
         * 登录失败
         */
        int ERROR_LOGIN = 104;
        /**
         * 未安装应用程序
         */
        int ERROR_NOT_INSTALL = 105;

        /**
         * 操作成功
         */
        int SUCCESS = 300;
    }

    /**
     * onActivityResult回调结果中 状态值
     */
    String KEY_CODE = "code";
    /**
     * onActivityResult回调结果中 内容
     */
    String KEY_CONTENT = "content";

}
