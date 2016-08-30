package com.iflytek.ihou.chang.app.wxapi;

import com.iflytek.platform.channel.WeixinAuthActivity;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 * <p>
 * 由于微信API的限制，消息只能通过{packageName + "wxapi.WXEntryActivity"}类接收
 *
 * @author dnwang
 * @version 2016/8/16,14:34
 * @see
 */
public class WXEntryActivity extends WeixinAuthActivity {
    /**
     * 此类请固定为:"包名.wxapi.WXEntryActivity";否则无法接受微信回调
     */
}
