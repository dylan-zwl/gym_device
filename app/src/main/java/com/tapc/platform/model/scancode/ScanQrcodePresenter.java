package com.tapc.platform.model.scancode;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.tapc.platform.model.scancode.ScanQrcodeModel.TcpListener;
import com.tapc.platform.model.scancode.dao.DeviceStatus;
import com.tapc.platform.model.scancode.dao.User;

import java.util.List;


public class ScanQrcodePresenter implements ScanQrcodeContract.Presenter {
    private ScanQrcodeContract.View mView;
    private ScanQrcodeModel mModel;
    private boolean isConnectServered = false;
    private boolean isFirstConnectServer = true;

    public ScanQrcodePresenter(Context context, @NonNull ScanQrcodeContract.View view) {
        mView = view;
        mModel = new ScanQrcodeModel();
        mModel.init(context);
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
        public void recvSportPlan(String userId, List<SportData> plan_load) {

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
                        isFirstConnectServer = false;
//                        uploadThirdSportsData();
                    }
                    SystemClock.sleep(20000);
                }
            }
        }).start();
    }
}
