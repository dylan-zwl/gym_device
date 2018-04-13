package com.tapc.platform.application;

import com.tapc.platform.entity.DeviceType;

/**
 * Created by Administrator on 2017/9/20.
 */

public class Config {
    public static DeviceType DEVICE_TYPE = DeviceType.TREADMILL;

    public static final String MEDIA_FILE = "/sdcard/tapc";

    public class Debug {
        //开启内存泄漏监听，正式版要设为false
        public static final boolean OPEN_REF_WATCHER = true;
    }

    //设备生产商编号
    public static final String MANUFACTURER_CODE = "sh001";
}
