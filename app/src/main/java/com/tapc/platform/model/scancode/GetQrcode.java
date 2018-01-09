package com.tapc.platform.model.scancode;

/**
 * Created by Administrator on 2018/1/9.
 */

public class GetQrcode {
    private class GetLoginType {
        private static final String QRCODE = "qrcode";
        private static final String RANDOMCODE = "randomcode";
        private static final String ADVERTISMENT = "advertisement";
    }

    private boolean mNeedChangeQrcode = true;


    public void setNeedChangeQrcode(boolean isNeed) {
        mNeedChangeQrcode = isNeed;
    }

}
