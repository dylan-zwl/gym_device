package com.tapc.platform.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.tapc.platform.ui.witget.MenuBar;

public class MenuService extends Service {
    private WindowManager.LayoutParams mMenuBarParams;
    private MenuBar mMenuBar;
    private WindowManager mWindowManager;
    private boolean isMenubarShowned = false;
    private LocalBinder mBinder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mBinder = new LocalBinder(this);
        initView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("sevice", "MenuService onDestroy");
        if (mMenuBar != null) {
            mMenuBar.dismiss();
            mMenuBar = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("sevice", "MenuService onBind");
        return mBinder;
    }

    private void initView() {
        mMenuBar = new MenuBar(this);
        mMenuBar.show();
    }

    public MenuBar getMenuBar() {
        return mMenuBar;
    }
}
