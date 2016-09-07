package org.pinwheel.platformsdk.callbacks;

import org.pinwheel.platformsdk.channel.ChannelType;

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
public interface Callback<T> {

    void call(ChannelType type, T t, String msg, int code);

}
