package com.tapc.platform.model.scancode;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tapc.platform.model.scancode.dao.request.HeartbeatPacketAck;
import com.tapc.platform.model.scancode.dao.request.OpenDeviceAck;
import com.tapc.platform.model.scancode.dao.request.OpenDeviceRequest;
import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 功能描述 : 获取数据中命令字节
     */
    public int getCommand(String jsonStr) {
        JSONObject jsonObject = null;
        int command = -1;
        try {
            jsonObject = new JSONObject(jsonStr);
            command = (int) jsonObject.get("command");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return command;
    }

    /**
     * 功能描述 : 获取请求打开设备的用户信息，device_id  需要与本设备相同，user_id 不为空才能开启设备
     */
    public ScanCodeUser getUser(int command, String jsonStr) {
        OpenDeviceRequest openDeviceRequest = GsonUtils.fromJson(jsonStr, OpenDeviceRequest.class);
        if (openDeviceRequest != null) {
            String deviceId = openDeviceRequest.getDevice_id();
            String userId = openDeviceRequest.getUser_id();
            if (!TextUtils.isEmpty(deviceId) && deviceId.equals(mDeviceId) && !TextUtils.isEmpty(userId)) {
                sendOpenDeviceStatus(command, openDeviceRequest.getUser_id(), openDeviceRequest.getScan_order_id(),
                        "0");
                ScanCodeUser user = new ScanCodeUser();
                user.setName(openDeviceRequest.getUser_name());
                user.setUserId(userId);
                user.setDeviceId(deviceId);
                user.setScan_order_id(openDeviceRequest.getScan_order_id());
                user.setAvatarUrl(openDeviceRequest.getUser_avatar());
                return user;
            }
        }
        sendOpenDeviceStatus(command, openDeviceRequest.getUser_id(), openDeviceRequest.getScan_order_id(), "1");
        return null;
    }

    /**
     * 功能描述 : 获取锻炼程序
     */
    public ExerciseProgram getExerciseProgram(String jsonStr) {
        ExerciseProgram exerciseProgram = GsonUtils.fromJson(jsonStr, ExerciseProgram.class);
        if (exerciseProgram != null && exerciseProgram.getDevice_id().equals(mDeviceId) && exerciseProgram
                .getAction_count() > 0) {
            int actionCount = exerciseProgram.getAction_count();
            List<String> plan_load = new ArrayList<>();
            if (actionCount > 0) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonStr);
                    for (int i = 0; i < actionCount; i++) {
                        String key = "plan_load" + (i + 1);
                        if (jsonObject.has(key)) {
                            String sportDataStr = jsonObject.get(key).toString();
                            if (sportDataStr != null && !sportDataStr.isEmpty()) {
                                plan_load.add(sportDataStr);
                            }
                        }
                    }
                    exerciseProgram.setPlan_load(plan_load);
                    return exerciseProgram;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 功能描述 : 心跳包回应
     *
     * @param : work_status ：设备使用状态
     */
    private String getHeartbeatJson(int command, String deviceId, int work_status) {
        HeartbeatPacketAck heartbeatPacket = new HeartbeatPacketAck(command, deviceId, work_status);
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
    private String getOpenDeviceAckJson(int command, String device_id, String user_id, String scan_order_id, String
            status) {
        OpenDeviceAck openDeviceAck = new OpenDeviceAck(command, device_id, user_id, scan_order_id, status);
        return toJson(openDeviceAck);
    }

    public void sendOpenDeviceStatus(int command, String user_id, String scan_order_id, String status) {
        String jsonStr = getOpenDeviceAckJson(command, mDeviceId, user_id, scan_order_id, status);
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
