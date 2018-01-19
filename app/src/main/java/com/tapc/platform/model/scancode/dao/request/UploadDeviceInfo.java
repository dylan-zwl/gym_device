package com.tapc.platform.model.scancode.dao.request;

import com.tapc.platform.entity.DeviceType;

public enum UploadDeviceInfo {
    T5517T(DeviceType.TREADMILL, "跑步机", "SH-T5517T", 0),

    B8900(DeviceType.BIKE, "健身车", "SH-8900U/R/E", 1),
    B8900ET(DeviceType.BIKE, "椭圆机", "SH-B8900ET", 2),
    B8900RT(DeviceType.BIKE, "卧式健身车", "SH-B8900RT", 3),
    B8900UT(DeviceType.BIKE, "立式健身车", "SH-B8900UT", 4),

    POWER(DeviceType.POWER, PowerDeviceInfor.TYPE[0], PowerDeviceInfor.MODEL[0], 0);

    private DeviceType mDeviceType;
    private String mType;
    private String mModel;
    private int mDeviceTypeId;
    private String mParameter;

    private UploadDeviceInfo(DeviceType deviceType, String type, String model, int deviceTypeId) {
        this.mDeviceType = deviceType;
        this.mDeviceTypeId = deviceTypeId;
        this.mType = type;
        this.mModel = model;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public DeviceType getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.mDeviceType = deviceType;
    }

    public int getDeviceTypeId() {
        return mDeviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.mDeviceTypeId = deviceTypeId;
    }

    public String getParameter() {
        return mParameter;
    }

    public void setParameter(String parameter) {
        this.mParameter = parameter;
    }
}