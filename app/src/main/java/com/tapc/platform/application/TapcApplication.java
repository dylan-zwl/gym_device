package com.tapc.platform.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.jni.Driver;
import com.tapc.platform.library.abstractset.ProgramSetting;
import com.tapc.platform.library.common.AppSettings;
import com.tapc.platform.library.common.BikeSystemSettings;
import com.tapc.platform.library.common.CommonEnum;
import com.tapc.platform.library.common.SystemSettings;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.controller.MachineController;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.service.LocalBinder;
import com.tapc.platform.service.MenuService;
import com.tapc.platform.utils.IntentUtils;
import com.tapc.platform.utils.NetUtils;

/**
 * Created by Administrator on 2017/8/21.
 */

public class TapcApplication extends Application {
    //    private RefWatcher mRefWatcher;
    private static TapcApplication mInstance;
    private MenuService mService;
    private KeyEvent mKeyEvent;

    private ProgramSetting mProgramSetting;
    private boolean isScreenOn = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler());

        //        内存泄漏检测工具
        if (Config.Debug.OPEN_REF_WATCHER) {
            //            mRefWatcher = LeakCanary.install(this);
        }

        Logger.init("tapc").methodCount(1).hideThreadInfo().logLevel(LogLevel.FULL).methodOffset(0);

        initControl(this);

        IntentUtils.stopService(this, MenuService.class);
        IntentUtils.bindService(this, MenuService.class, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocalBinder binder = (LocalBinder) service;
                if (name.getClassName().equals(MenuService.class.getName())) {
                    mService = (MenuService) binder.getService();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        initDeviceId();
    }

    private void initDeviceId() {
        String id = NetUtils.getLocalMacAddress(this);
        if (!TextUtils.isEmpty(id)) {
            ConfigModel.setDeviceId(this, id);
        }
    }

    private void initControl(Context context) {
        Driver.openUinput(Driver.UINPUT_DEVICE_NAME);
        Driver.initCom(CommonEnum.Platform.S700.getPlatform(), 115200);

        SystemSettings systemSettings = null;
        if (Config.DEVICE_TYPE == DeviceType.TREADMILL) {
            systemSettings = new TreadmillSystemSettings();
        } else if (Config.DEVICE_TYPE == DeviceType.BIKE) {
            systemSettings = new BikeSystemSettings();
        }
        if (systemSettings != null) {
            systemSettings.Load(this, null);
            //            systemSettings.mPath = "/mnt/sdcard/premierprograms.db";
            AppSettings.setPlatform(CommonEnum.Platform.S700);
            AppSettings.setLoopbackMode(false);
            MachineController controller = MachineController.getInstance();
            controller.initController(this);
            controller.start();
            WorkOuting.getInstance().initWorkOuting(controller.getMachineOperateListener(), this);
        }
    }

    //    public void addRefWatcher(Object watchedReference) {
    //        if (mRefWatcher != null) {
    //            mRefWatcher.watch(watchedReference);
    //        }
    //    }
    //
    //    public RefWatcher getRefWatcher() {
    //        return mRefWatcher;
    //    }

    public static TapcApplication getInstance() {
        return mInstance;
    }

    public MenuService getService() {
        return mService;
    }

    //    public Driver getKeyEvent() {
    //        return mDriver;
    //    }
    //
    //    public Class<?> getHomeActivity() {
    //        return mHomeActivity;
    //    }
    //
    //    public void setHomeActivity(Class<?> homeActivity) {
    //        this.mHomeActivity = homeActivity;
    //    }
    //
    public ProgramSetting getProgramSetting() {
        return mProgramSetting;
    }

    public void setProgramSetting(ProgramSetting programSetting) {
        this.mProgramSetting = programSetting;
    }

    public boolean isScreenOn() {
        return isScreenOn;
    }

    public void setScreenOn(boolean screenOn) {
        isScreenOn = screenOn;
    }
}
