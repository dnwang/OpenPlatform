package org.pinwheel.platformsdk.entity;

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
         * 微信返回使用的临时状态值，不具备任何属性
         */
        int UNKNOWN_WEIXIN_RETURN = -100;

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

    String DEFAULT_SINA_REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";

}
