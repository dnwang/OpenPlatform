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
public interface StateCodes {

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
