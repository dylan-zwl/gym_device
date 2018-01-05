package com.tapc.platform.service;

import android.app.Service;
import android.os.Binder;

/**
 * Created by Administrator on 2016/12/20.
 */

public class LocalBinder extends Binder {
    private Service mService;

    public LocalBinder(Service service) {
        this.mService = service;
    }

    public void setService(Service service) {
        this.mService = service;
    }

    public Service getService() {
        return mService;
    }
}
