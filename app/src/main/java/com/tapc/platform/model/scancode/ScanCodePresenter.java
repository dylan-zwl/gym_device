package com.tapc.platform.model.scancode;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tapc.platform.application.Config;
import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.library.common.BikeSystemSettings;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.scancode.ScanCodeModel.ScanCodeListener;
import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.model.scancode.entity.DeviceStatus;
import com.tapc.platform.model.scancode.entity.PowerDeviceInfor;
import com.tapc.platform.model.scancode.entity.UploadDeviceInfo;
import com.tapc.platform.utils.GsonUtils;

import java.util.HashMap;
import java.util.Map;

import static com.tapc.platform.library.common.SystemSettings.mContext;


public class ScanCodePresenter implements ScanCodeContract.Presenter {
    private ScanCodeContract.View mView;
    private ScanCodeModel mModel;
    private boolean isConnectServered = false;

    public ScanCodePresenter(Context context, @NonNull ScanCodeContract.View view, DeviceType deviceType) {
        mView = view;
        mModel = new ScanCodeModel(context.getApplicationContext(), deviceType);
        mModel.setDeviceId(ConfigModel.getDeviceId(context, ""));
        mModel.setUploadDeviceInfor(getDeviceParameter(deviceType));
        mModel.setTcpListener(mTcpListener);
    }

    @Override
    public void start() {
        mModel.connectServer();
        uploadLoaclData();
    }

    @Override
    public void stop() {
        if (mUploadLocalDataThread != null) {
            mUploadLocalDataThread.interrupt();
        }
        mModel.disConnectServer();
    }

    @Override
    public void updateDeviceStatus() {
        mModel.updateDeviceStatus();
    }


    @Override
    public String getPassword() {
        return mModel.getLoginPassword();
    }

    private ScanCodeListener mTcpListener = new ScanCodeListener() {

        @Override
        public void showQrcode(String qrcodeStr) {
            mView.showQrcode(qrcodeStr);
        }

        @Override
        public void connectServerResult(boolean isSuccess) {
            mView.connectServerResult(isSuccess);
            isConnectServered = isSuccess;
        }

        @Override
        public void openDevice(ScanCodeUser user) {
            mView.openDevice(user);
        }

        @Override
        public void recvSportPlan(ExerciseProgram exerciseProgram) {
            mView.recvSportPlan(exerciseProgram);
        }

        @Override
        public int getWorkStatus() {
            return mView.getDeviceStatus();
        }

        @Override
        public void seveLoginRandomcode(String randomcode) {
            ConfigModel.setLoginRandomcode(mContext, randomcode);
        }
    };

    /**
     * 功能描述 : 上传 没有成功上传的运动数据
     */
    private Thread mUploadLocalDataThread;

    private void uploadLoaclData() {
        mUploadLocalDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (isConnectServered && mView.getDeviceStatus() == DeviceStatus.NO_USE) {
                            //                        uploadThirdSportsData();
                        }
                        Thread.sleep(20000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mUploadLocalDataThread.start();
    }

    /**
     * 功能描述 : 获取设备参数信息
     */
    public UploadDeviceInfo getDeviceParameter(DeviceType deviceType) {
        Map<String, Object> map = new HashMap<>();
        switch (deviceType) {
            case TREADMILL:
                map.put("time", (60 * 120));
                map.put("speed", TreadmillSystemSettings.MAX_SPEED);
                map.put("incline", TreadmillSystemSettings.MAX_INCLINE);
                break;
            case BIKE:
                map.put("time", (60 * 120));
                map.put("watt", BikeSystemSettings.MAX_WATT);
                map.put("resistance", BikeSystemSettings.MAX_RESISTANCE);
                break;
            case POWER:
                map.put("times", 100);
                map.put("heavy", 150);
                map.put("cooldown", 120);
                break;
            case DOOR:
                break;
        }
        String parameter = GsonUtils.toJson(map);
        int deviceTypeId = ConfigModel.getDeviceTypeId(mContext, 0);
        UploadDeviceInfo uploadDeviceInfo = UploadDeviceInfo.T5517T;
        switch (deviceType) {
            case TREADMILL:
                for (UploadDeviceInfo info : UploadDeviceInfo.values()) {
                    if (info.getDeviceType() == DeviceType.TREADMILL && info.getDeviceTypeId() == deviceTypeId) {
                        info.setParameter(parameter);
                        uploadDeviceInfo = info;
                    }
                }
                break;
            case BIKE:
                for (UploadDeviceInfo info : UploadDeviceInfo.values()) {
                    if (info.getDeviceType() == DeviceType.BIKE && info.getDeviceTypeId() == deviceTypeId) {
                        info.setParameter(parameter);
                        uploadDeviceInfo = info;
                    }
                }
                break;
            case POWER:
                UploadDeviceInfo info = UploadDeviceInfo.POWER;
                info.setType(PowerDeviceInfor.TYPE[deviceTypeId]);
                info.setModel(PowerDeviceInfor.MODEL[deviceTypeId]);
                info.setParameter(parameter);
                uploadDeviceInfo = info;
                break;
            case DOOR:
                uploadDeviceInfo = UploadDeviceInfo.DOOR;
                break;
        }
        uploadDeviceInfo.setManufacturerCode(Config.MANUFACTURER_CODE);
        return uploadDeviceInfo;
    }
}
