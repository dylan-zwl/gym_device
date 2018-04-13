package com.tapc.platform.ui.witget.scancode;

import com.tapc.platform.model.healthcat.BaseCtlModel;

public interface ScanCodeContract {

    interface View {
        void connectServerResult(boolean isSuccess);

        void showQrcode(String qrcode);

        void loginStatus(boolean isSuccess);

        int getDeviceStatus();

        void serverSetLock(boolean lock);
    }

    interface Presenter {
        void start();

        void stop();

        String getPassword();

        void setListener(BaseCtlModel.BaseListener listener);
    }
}
