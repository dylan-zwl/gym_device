package com.tapc.platform.entity;

/**
 * Created by Administrator on 2016/11/13.
 */

public class DeviceWorkout {
    public static final String ACTION = "device_workout_ctl";
    public static final String MSG_WHAT = "device_workout_ctl";
    private static final int BASE = 0;
    public static final int COUNTDDOWN = BASE + 1;
    public static final int START = BASE + 2;
    public static final int STOP = BASE + 3;
    public static final int PAUSE = BASE + 4;
    public static final int RESUME = BASE + 5;
}
