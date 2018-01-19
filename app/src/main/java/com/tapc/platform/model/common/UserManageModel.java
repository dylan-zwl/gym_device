package com.tapc.platform.model.common;

import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;

/**
 * Created by Administrator on 2018/1/19.
 */

public class UserManageModel {
    private static UserManageModel sUserManageModel;
    private boolean isLogined = false;
    private ScanCodeUser mScanCodeUser;

    private UserManageModel() {
    }

    public static UserManageModel getInstance() {
        if (sUserManageModel == null) {
            synchronized (UserManageModel.class) {
                if (sUserManageModel == null) {
                    sUserManageModel = new UserManageModel();
                }
            }
        }
        return sUserManageModel;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void login() {
        isLogined = true;
    }

    public void logout() {
        isLogined = false;
        mScanCodeUser = null;
    }

    public ScanCodeUser getScanCodeUser() {
        return mScanCodeUser;
    }

    public void setScanCodeUser(ScanCodeUser scanCodeUser) {
        this.mScanCodeUser = scanCodeUser;
    }
}
