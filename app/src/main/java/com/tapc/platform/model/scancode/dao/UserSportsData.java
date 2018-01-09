package com.tapc.platform.model.scancode.dao;

import java.util.List;

public class UserSportsData<T> {
    private String device_id;
    private String user_id;
    private String plan_id;
    private String sport_type;
    private String time;
    private String calorie;
    private String distance;
    private String weight;
    private List<T> sport_data;
    private String date;
    private String order_number;
    private String customer_id;
    private int sport_flg;

    private long open_time;
    private long start_time;
    private long stop_time;
    private long use_device_time;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getSport_type() {
        return sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public List<T> getSport_data() {
        return sport_data;
    }

    public void setSport_data(List<T> sport_data) {
        this.sport_data = sport_data;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getSport_flg() {
        return sport_flg;
    }

    public void setSport_flg(int sport_flg) {
        this.sport_flg = sport_flg;
    }

    public long getOpen_time() {
        return open_time;
    }

    public void setOpen_time(long open_time) {
        this.open_time = open_time;
    }

    public long getUse_device_time() {
        return use_device_time;
    }

    public void setUse_device_time(long use_device_time) {
        this.use_device_time = use_device_time;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getStop_time() {
        return stop_time;
    }

    public void setStop_time(long stop_time) {
        this.stop_time = stop_time;
    }

}
