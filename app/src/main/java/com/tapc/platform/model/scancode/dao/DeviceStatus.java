package com.tapc.platform.model.scancode.dao;

/**
 * Created by Administrator on 2018/1/9.
 */

public class DeviceStatus {
    // 0代表空闲，1代表使用中， 2代表断网中，3代表异常，4代表不存在该设备
    public static final int NO_USE = 0;
    public static final int USING = 1;
    public static final int NO_NET = 2;
    public static final int ERROR = 3;
    public static final int NO_DEVICE = 4;
}
