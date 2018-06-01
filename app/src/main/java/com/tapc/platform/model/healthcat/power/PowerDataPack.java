package com.tapc.platform.model.healthcat.power;

import com.tapc.platform.model.healthcat.BaseDataPack;

/**
 * Created by Administrator on 2018/4/11.
 */

public class PowerDataPack extends BaseDataPack {
    public PowerDataPack(byte sid) {
        super(sid);
    }

    public byte[] getHeartbeatData(byte command, PowerData powerData) {
        byte[] data = new byte[10];
        if (powerData != null) {
            data[0] = powerData.getStatus();

            float weight = powerData.getWeight();
            data[1] = (byte) weight;
            data[2] = (byte) (weight * 10 % 10);

            byte[] times = intToBytes(powerData.getTimes());
            data[3] = times[1];
            data[4] = times[0];

            byte[] time = intToBytes(powerData.getRunTime());
            data[5] = time[1];
            data[6] = time[0];

            byte[] calorie = intToBytes(powerData.getCalorie());
            data[7] = calorie[1];
            data[8] = calorie[0];

            data[9] = (byte) powerData.getDelayAckTime();
        }
        return getDataStream(command, data);
    }
}
