package com.tapc.platform.model.scancode;

import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;

public interface ScanCodeContract {

    interface View {
        void connectServerResult(boolean isSuccess);

        void showQrcode(String qrcode);

        void openDevice(ScanCodeUser user);

        void recvSportPlan(ExerciseProgram exerciseProgram);

        int getDeviceStatus();
    }

    interface Presenter {
        void start();

        void stop();

        void updateDeviceStatus();

        String getPassword();
    }
}
