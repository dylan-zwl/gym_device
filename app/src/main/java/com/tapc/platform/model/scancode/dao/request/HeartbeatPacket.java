package com.tapc.platform.model.scancode.dao.request;

public class HeartbeatPacket {
    private int command;
    private String device_id;
    private int work_status;

    public HeartbeatPacket(int command, String device_id, int work_status) {
        this.command = command;
        this.device_id = device_id;
        this.work_status = work_status;
    }

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

    public int getWork_status() {
        return work_status;
    }

    public void setWork_status(int work_status) {
        this.work_status = work_status;
    }

}
