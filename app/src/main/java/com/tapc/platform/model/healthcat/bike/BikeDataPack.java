package com.tapc.platform.model.healthcat.bike;

import com.tapc.platform.model.healthcat.BaseDataPack;
import com.tapc.platform.model.healthcat.Command;

/**
 * Created by Administrator on 2018/4/11.
 */

public class BikeDataPack extends BaseDataPack {
    public BikeDataPack(byte sid) {
        super(sid);
    }

    public byte[] getReadInfoData(BikeData bikeData) {
        byte[] data = new byte[2];
        if (bikeData != null) {
            data[0] = (byte) bikeData.getDiameter();
            data[1] = (byte) bikeData.getResistance();
        }
        return getDataStream(Command.Bike.D_READ_INFO, data);
    }

    public byte[] getHeartbeatData(byte command, BikeData bikeData) {
        byte[] data = new byte[15];
        if (bikeData != null) {
            data[0] = bikeData.getStatus();

            float speed = bikeData.getSpeed();
            data[1] = (byte) speed;
            data[2] = (byte) (speed * 10 % 10);

            byte[] rounds = intToBytes(bikeData.getRounds());
            data[3] = rounds[1];
            data[4] = rounds[0];

            data[5] = (byte) bikeData.getHeart();
            data[6] = (byte) bikeData.getResistance();

            byte[] calorie = intToBytes(bikeData.getCalorie());
            data[7] = calorie[1];
            data[8] = calorie[0];

            data[9] = (byte) bikeData.getDelayAckTime();

            byte[] distance = intToBytes(bikeData.getDistance());
            data[10] = distance[1];
            data[11] = distance[0];

            byte[] time = intToBytes(bikeData.getRunTime());
            data[12] = time[1];
            data[13] = time[0];

            data[14] = (byte) bikeData.getDiameter();
        }
        return getDataStream(command, data);
    }
}
