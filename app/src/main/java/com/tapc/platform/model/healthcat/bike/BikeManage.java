package com.tapc.platform.model.healthcat.bike;

import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseDataPack;
import com.tapc.platform.model.healthcat.Command;
import com.tapc.platform.model.healthcat.SID;
import com.tapc.platform.model.tcp.TcpClient;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BikeManage extends BaseCommunicationManage {
    private BikeDataPack mBikeDataPack;

    public BikeManage() {
        super();
    }

    @Override
    public BaseDataPack getDataPack() {
        mBikeDataPack = new BikeDataPack(SID.BIKE);
        return mBikeDataPack;
    }

    public BikeManage(TcpClient tcpClient, String deviceId) {
        super(tcpClient, deviceId);
    }

    public boolean sendHeartbeat() {
        byte[] data = mBikeDataPack.getDataStream(Command.Bike.D_UPLOAD_INFO, null);
        return sendData(data);
    }
}
