package com.tapc.platform.model.scancode.dao;

import java.util.List;

public class SportsMenu<T> {
    private int command;
    private String device_id;
    private String user_id;
    private String type;
    private String plan_id;
    private int action_count;
    private List<T> plan_load;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAction_count() {
        return action_count;
    }

    public void setAction_count(int action_count) {
        this.action_count = action_count;
    }

    public List<T> getPlan_load() {
        return plan_load;
    }

    public void setPlan_load(List<T> plan_load) {
        this.plan_load = plan_load;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }
}
