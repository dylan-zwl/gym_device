package com.tapc.platform.model.scancode;

import com.google.gson.Gson;
import com.tapc.platform.model.scancode.dao.request.HeartbeatPacket;
import com.tapc.platform.model.scancode.dao.response.OpenDeviceAck;
import com.tapc.platform.model.tcp.TcpClient;

import io.reactivex.annotations.NonNull;

/**
 * Created by Administrator on 2018/1/10.
 */

public class CommunicationManage {
    public class Command {
        public static final int HEARTBEAT = 1;
        public static final int OPEN_DEVICE = 2;
        public static final int SPORTS_PLAN = 3;
        public static final int THIRD_OPEN_DEVICE = 4;
    }

    private TcpClient mTcpClient;
    private String mDeviceId;

    public CommunicationManage(String deviceId) {
        mDeviceId = deviceId;
    }

    public void setClient(@NonNull TcpClient tcpClient) {
        mTcpClient = tcpClient;
    }


    private void uploadDeviceInformation() {

    }


    /**
     * 功能描述 : 心跳包回应
     *
     * @param : work_status ：设备使用状态
     */
    private String getHeartbeatJson(int command, String deviceId, int work_status) {
        HeartbeatPacket heartbeatPacket = new HeartbeatPacket(command, deviceId, work_status);
        return toJson(heartbeatPacket);
    }

    public void sendHeartbeat(int work_status) {
        String jsonStr = getHeartbeatJson(Command.HEARTBEAT, mDeviceId, work_status);
        sendData(jsonStr);
    }

    /**
     * 功能描述 : 打开设备回应
     *
     * @param :
     */
    private String getOpenDeviceAckJson(int command, String device_id, String user_id, String status) {
        OpenDeviceAck openDeviceAck = new OpenDeviceAck(command, device_id, user_id, status);
        return toJson(openDeviceAck);
    }

    public void sendOpenDeviceStatus(String user_id, String status) {
        String jsonStr = getOpenDeviceAckJson(Command.OPEN_DEVICE, mDeviceId, user_id, status);
        sendData(jsonStr);
    }

    public <T> T fromJson(String jsonStr, Class<T> classOfT) {
        return new Gson().fromJson(jsonStr, classOfT);
    }

    public String toJson(Object object) {
        return new Gson().toJson(object).toString();
    }

    public void sendData(String jsonStr) {
        if (jsonStr != null && mTcpClient != null) {
            mTcpClient.sendData(jsonStr);
        }
    }

    public void sendData(byte[] dataBuffer) {
        if (mTcpClient != null) {
            mTcpClient.sendData(dataBuffer);
        }
    }
}
