package com.tapc.platform.ui.witget.scancode;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.bike.BikeCtlModel;


public class ScanCodePresenter implements ScanCodeContract.Presenter {
    private ScanCodeContract.View mView;
    private BikeCtlModel mModel;
    private boolean isConnectServered = false;

    public ScanCodePresenter(Context context, @NonNull final ScanCodeContract.View view, DeviceType deviceType) {
        mView = view;
        mModel = new BikeCtlModel(context.getApplicationContext());
        mModel.setDeviceId(ConfigModel.getDeviceId(context, ""));
        mModel.setBikeCtlLister(new BikeCtlModel.BikeCtlLister() {
            @Override
            public void serverReadInfo() {

            }

            @Override
            public void serverReadStatus() {

            }

            @Override
            public void serverSetResistance(byte resistance) {

            }

            @Override
            public void serverSetRunStatus(boolean isStart) {

            }

            @Override
            public void serverSetLock(boolean lock) {
                view.serverSetLock(lock);
            }

            @Override
            public void showQrcode(String qrcodeStr) {
                view.showQrcode(qrcodeStr);
            }

            @Override
            public void connectServerResult(boolean isSuccess) {
                view.connectServerResult(isSuccess);
                if (isSuccess) {
                    mView.loginStatus(mModel.login());
                }
            }

            @Override
            public void login(boolean isSuccess) {
                if (isSuccess) {
//                    view.showQrcode(qrcodeStr);
                }
            }
        });
    }

    @Override
    public void start() {
        mModel.connectServer();
    }

    @Override
    public void stop() {
        mModel.disConnectServer();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setListener(BaseCtlModel.BaseListener listener) {
        mModel.setBikeCtlLister((BikeCtlModel.BikeCtlLister) listener);
    }

}
