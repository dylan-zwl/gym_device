package com.tapc.platform.model.healthcat.power;

/**
 * Created by Administrator on 2018/4/13.
 */

public class PowerData {
    private int heartTime;

    private byte status;
    private int weight;
    private int times;
    private int runTime;
    private int calorie;
    private int delayAckTime;

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getDelayAckTime() {
        return delayAckTime;
    }

    public void setDelayAckTime(int delayAckTime) {
        this.delayAckTime = delayAckTime;
    }
}
