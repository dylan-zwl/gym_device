package com.tapc.platform.broadcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;


public class NetWorkReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null) {
            EventBus.getDefault().post(activeInfo);
        }
    }
}
