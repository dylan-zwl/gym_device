package com.tapc.platform.application;

import android.util.Log;

import com.tapc.platform.entity.EventEntity;

import org.greenrobot.eventbus.EventBus;

import java.lang.Thread.UncaughtExceptionHandler;


public class AppExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mDefaultUEH;

    public AppExceptionHandler() {
        this.mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d("AppExceptionHandler", "app error exit");
        EventBus.getDefault().post(new EventEntity.ReloadApp());
        // defaultUEH.uncaughtException(thread, ex);
    }

}
