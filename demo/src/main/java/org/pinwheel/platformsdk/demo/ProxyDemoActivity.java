package org.pinwheel.platformsdk.demo;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import org.pinwheel.platformsdk.channel.ChannelType;
import org.pinwheel.platformsdk.entity.Constants;
import org.pinwheel.platformsdk.entity.PayInfo;
import org.pinwheel.platformsdk.entity.ShareContent;
import org.pinwheel.platformsdk.utils.Tools;
import org.pinwheel.platformsdk.wrapper.PlatformProxy;

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
public class ProxyDemoActivity extends Activity {

    /**
     * 分享
     */
    private final View.OnClickListener shareClick = view -> {
        final ChannelType type = getSelectedType();
        final ShareContent content = new ShareContent.Builder()
                .title("share sdk")
                .content("share from ichang")
//                .image("http://www.weipet.cn/common/images/pic/a347.jpg")
                .image(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .image(R.mipmap.ic_launcher)
                .linkUrl("http://172.16.4.3/group1/M00/01/AF/rBAEA1bUaTmAHc7GAIrbl2MfKI8642.mp3")
                .mediaUrl("http://172.16.4.196:8080/share/df917a22bd1f4c7380340cab94af2061.shtml")
                .create();
        PlatformProxy.share(ProxyDemoActivity.this, type, content, (channelType, obj, msg, code) -> {
            Toast.makeText(getApplicationContext(), Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 登录
     */
    private final View.OnClickListener loginClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.login(ProxyDemoActivity.this, type, (channelType, user, msg, code) -> {
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = user.id + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 获取朋友列表
     */
    private final View.OnClickListener getFriendsClick = view -> {
        final ChannelType type = getSelectedType();
        PlatformProxy.getFriends(ProxyDemoActivity.this, type, (channelType, users, msg, code) -> {
            String tips = Tools.getSimpleTips(code) + ", " + msg;
            if (Constants.Code.SUCCESS == code) {
                tips = users.size() + ", " + tips;
            }
            Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 支付
     */
    private final View.OnClickListener payClick = view -> {
        final ChannelType type = getSelectedType();
        final PayInfo payInfo = new PayInfo();
        PlatformProxy.pay(ProxyDemoActivity.this, type, payInfo, (channelType, isSuccess, msg, code) -> {
            Toast.makeText(getApplicationContext(), isSuccess + ", " + Tools.getSimpleTips(code) + ", " + msg, Toast.LENGTH_SHORT).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_share).setOnClickListener(shareClick);
        findViewById(R.id.btn_login).setOnClickListener(loginClick);
        findViewById(R.id.btn_friends).setOnClickListener(getFriendsClick);
        findViewById(R.id.btn_pay).setOnClickListener(payClick);
    }

    private ChannelType getSelectedType() {
        ChannelType channelType = null;
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.navi_platforms);
        final int size = viewGroup.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof RadioButton) {
                if (((RadioButton) v).isChecked()) {
                    Object tag = v.getTag();
                    if (null != tag) {
                        channelType = ChannelType.valueOf(String.valueOf(tag));
                    }
                    break;
                }
            }
        }
        return channelType;
    }

}
