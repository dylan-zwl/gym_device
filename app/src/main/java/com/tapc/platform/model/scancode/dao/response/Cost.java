package com.tapc.platform.model.scancode.dao.response;

/**
 * Created by Administrator on 2018/1/23.
 */

public class Cost {
    private int cost_site_id;

    private float busy_start_cost;
    private int busy_start_time;
    private float busy_price;

    private float free_start_cost;
    private int free_start_time;
    private float free_price;

    private CostBusyTime[] times;


    public int getCost_site_id() {
        return cost_site_id;
    }

    public void setCost_site_id(int cost_site_id) {
        this.cost_site_id = cost_site_id;
    }

    public float getBusy_start_cost() {
        return busy_start_cost;
    }

    public void setBusy_start_cost(float busy_start_cost) {
        this.busy_start_cost = busy_start_cost;
    }

    public int getBusy_start_time() {
        return busy_start_time;
    }

    public void setBusy_start_time(int busy_start_time) {
        this.busy_start_time = busy_start_time;
    }

    public float getBusy_price() {
        return busy_price;
    }

    public void setBusy_price(float busy_price) {
        this.busy_price = busy_price;
    }

    public float getFree_start_cost() {
        return free_start_cost;
    }

    public void setFree_start_cost(float free_start_cost) {
        this.free_start_cost = free_start_cost;
    }

    public int getFree_start_time() {
        return free_start_time;
    }

    public void setFree_start_time(int free_start_time) {
        this.free_start_time = free_start_time;
    }

    public float getFree_price() {
        return free_price;
    }

    public void setFree_price(float free_price) {
        this.free_price = free_price;
    }

    public CostBusyTime[] getTimes() {
        return times;
    }

    public void setTimes(CostBusyTime[] times) {
        this.times = times;
    }
}
