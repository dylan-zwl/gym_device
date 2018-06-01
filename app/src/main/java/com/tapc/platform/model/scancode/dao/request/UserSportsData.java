package com.tapc.platform.model.scancode.dao.request;

import java.util.List;

public class UserSportsData<T> {
    //设备id
    private String device_id;
    //用户id
    private String user_id;

    //用户运动类型
    private String sport_type;

    //扫码id 第三接入订单号
    private String scan_order_id;

    //上传的时间记录，默认null
    private String date;
    //打开设备时间
    private long open_time;
    //开启运动时间
    private long start_time;
    //停止运动时间
    private long stop_time;
    //使用设备总时间
    private long use_device_time;

    //运动数据
    private long time;
    private float calorie;
    private float distance;

    //特有参数
    //重训的次数
    private String times;

    //运动阶段数据
    private String plan_id;
    //运动详细数据
    private List<T> sport_data;

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

    public String getScan_order_id() {
        return scan_order_id;
    }

    public void setScan_order_id(String scan_order_id) {
        this.scan_order_id = scan_order_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public List<T> getSport_data() {
        return sport_data;
    }

    public void setSport_data(List<T> sport_data) {
        this.sport_data = sport_data;
    }


/*
    运动阶段数据

    ①　跑步机（有氧）
    [
        {
            "incline": "0.0", //当期阶段平均坡度
                "speed": "1.5", //当前阶段平均速度
                "time": "4" //当前阶段耗时
        }
    ]
    ②　健身车（有氧）
    [
        {
            "speed": "36.6", //当前阶段平均速度
                "time": "39",	//当前阶段耗时
                "watt": "184892.0" //当前阶段平均瓦特
        }
    ]
    ③　重训（无氧）
    [
        {
            "cooldown": "", //当前阶段休息时间（秒）
                "heavy": "20.0",//当前阶段重量（公斤）
                "times": "4"//当前阶段次数
        },
    ]
*/
}
