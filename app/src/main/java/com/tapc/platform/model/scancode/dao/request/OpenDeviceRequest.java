package com.tapc.platform.model.scancode.dao.request;

public class OpenDeviceRequest {
    private int command;
    private String device_id;
    private String user_id;
    private String user_name;
    private String user_avatar;
    private String scan_order_id;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getScan_order_id() {
        return scan_order_id;
    }

    public void setScan_order_id(String scan_order_id) {
        this.scan_order_id = scan_order_id;
    }
}
