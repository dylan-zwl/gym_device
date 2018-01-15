package com.tapc.platform.model.scancode;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.scancode.ScanQrcodeModel.TcpListener;
import com.tapc.platform.model.scancode.dao.request.DeviceStatus;
import com.tapc.platform.model.scancode.dao.response.SportsMenu;
import com.tapc.platform.model.scancode.dao.response.User;


public class ScanQrcodePresenter implements ScanQrcodeContract.Presenter {
    private ScanQrcodeContract.View mView;
    private ScanQrcodeModel mModel;
    private boolean isConnectServered = false;

    public ScanQrcodePresenter(Context context, @NonNull ScanQrcodeContract.View view, DeviceType deviceType) {
        mView = view;
        mModel = new ScanQrcodeModel(context.getApplicationContext(), deviceType);
        mModel.setTcpListener(mTcpListener);
    }

    @Override
    public void start() {
        mModel.connectServer();
        uploadLoaclData();
    }

    @Override
    public void stop() {

    }

    @Override
    public void updateDeviceStatus() {
        mModel.updateDeviceStatus();
    }


    private TcpListener mTcpListener = new TcpListener() {

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
        public void recvSportPlan(String userId, SportsMenu plan_load) {

        }


        @Override
        public int getWorkStatus() {
            return mView.getDeviceStatus();
        }
    };


    private void uploadLoaclData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isConnectServered && mView.getDeviceStatus() == DeviceStatus.NO_USE) {
                        //                        uploadThirdSportsData();
                    }
                    SystemClock.sleep(20000);
                }
            }
        }).start();
    }
}
