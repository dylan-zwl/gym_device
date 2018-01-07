package com.tapc.platform.broadcast.send;

import android.content.Context;
import android.content.Intent;

import com.tapc.platform.entity.DeviceWorkout;

/**
 * Created by Administrator on 2016/11/13.
 */

public class WorkoutBroadcase {
    public static void send(Context context, int msgWhat) {
        Intent intent = new Intent();
        intent.setAction(DeviceWorkout.ACTION);
        intent.putExtra(DeviceWorkout.MSG_WHAT, msgWhat);
        context.sendBroadcast(intent);
    }
}
