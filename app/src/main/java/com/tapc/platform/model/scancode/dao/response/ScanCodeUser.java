package com.tapc.platform.model.scancode.dao.response;


import com.tapc.platform.model.common.UserManageModel;


public class ScanCodeUser extends UserManageModel.User {
    private String name;
    private String avatarUrl;
    private String deviceId;
    private String userId;
    private String planId;
    private String scan_order_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "**";
        }
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getScan_order_id() {
        return scan_order_id;
    }

    public void setScan_order_id(String scan_order_id) {
        this.scan_order_id = scan_order_id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
