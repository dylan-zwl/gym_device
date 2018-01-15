package com.tapc.platform.model.scancode;

import com.tapc.platform.model.scancode.dao.response.SportsMenu;
import com.tapc.platform.model.scancode.dao.response.User;

public interface ScanQrcodeContract {

    interface View {
        void connectServerResult(boolean isSuccess);

        void showQrcode(String qrcode);

        void openDevice(User user);

        void recvSportPlan(SportsMenu plan_load);

        int getDeviceStatus();

        void loginPassword(String password);
    }

    interface Presenter {
        void start();

        void stop();

        void updateDeviceStatus();
    }
}
