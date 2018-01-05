package com.tapc.platform.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapc.platform.entity.BluetoothConnectStatus;

import org.greenrobot.eventbus.EventBus;

public class BlueReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            EventBus.getDefault().post(new BluetoothConnectStatus(true));
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
            EventBus.getDefault().post(new BluetoothConnectStatus(false));
        }
    }
}
