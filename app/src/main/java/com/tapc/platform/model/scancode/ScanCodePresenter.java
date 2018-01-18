package com.tapc.platform.model.scancode;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.scancode.ScanCodeModel.ScanCodeListener;
import com.tapc.platform.model.scancode.dao.request.DeviceStatus;
import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.User;


public class ScanCodePresenter implements ScanCodeContract.Presenter {
    private ScanCodeContract.View mView;
    private ScanCodeModel mModel;
    private boolean isConnectServered = false;

    public ScanCodePresenter(Context context, @NonNull ScanCodeContract.View view, DeviceType deviceType) {
        mView = view;
        mModel = new ScanCodeModel(context.getApplicationContext(), deviceType);
        mModel.setDeviceId(ConfigModel.getDeviceId(context, ""));
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
        public void openDevice(User user) {
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
}
