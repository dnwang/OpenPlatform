package org.pinwheel.platformsdk.channel;

import org.pinwheel.platformsdk.callbacks.Callback;
import org.pinwheel.platformsdk.entity.PayInfo;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/13/16,08:06
 * @see
 */
public interface Payable {

    void pay(PayInfo payInfo, Callback<Object> callback);

}
