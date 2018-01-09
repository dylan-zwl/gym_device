package com.tapc.platform.model.scancode;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tapc.platform.model.scancode.dao.User;
import com.tapc.platform.model.scancode.url.ScanCodeUrl;
import com.tapc.platform.model.tcp.SocketListener;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.PreferenceHelper;

import java.net.InetAddress;

public class ScanQrcodeModel {
    public static final String IP = "112.74.87.166";
    public static final int PORT = 1234;
    private Context mContext;
    private String mDeviceId = "";
    private boolean isUploadDeviceInfor = false;
    private TcpClient mTcpClient;
    private String mQrcodeStr = "";
    private String mLoginPassword;
    public static boolean mNeedChangeQrcode = true;
    private boolean mCurrentConnectStatus = false;
    private TcpListener mTcpListener;

    public interface TcpListener {
        void showQrcode(String qrcodeStr);

        void connectServerResult(boolean isSuccess);

        void openDevice(User user);

        void recvSportPlan(String userId, List<SportData> plan_load);

        int getWorkStatus();
    }

    public ScanQrcodeModel(Context context) {
        mContext = context;
    }

    /**
     * 功能描述 : 连接服务器
     */
    public void connectServer() {
        new Thread(mRunnable).start();
        new Thread(mQrRunnable).start();
    }

    private void connectServerResult(boolean isSuccess) {
        if (!isSuccess) {
            stopTcpClient();
        }
        if (mTcpListener != null) {
            mTcpListener.connectServerResult(isSuccess);
        }
    }

    private boolean connect() {
        boolean isConnected = false;
        try {
            if (mTcpListener != null) {
                mTcpListener.showQrcode(null);
            }
            if (NetUtils.isConnected(mContext) && !TextUtils.isEmpty(mDeviceId)) {
                Log.d("tcp connect", "start  device id : " + mDeviceId);
                String ip = GetInetAddress(ScanCodeUrl.BASE_SERVER);
                if (TextUtils.isEmpty(ip)) {
                    ip = IP;
                }
                mTcpClient = new TcpClient();
                mTcpClient.setListener(mSoketListener);
                isConnected = mTcpClient.connect(ip, PORT, 4000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    if (mCurrentConnectStatus == false) {
                        stopTcpClient();
                        mCurrentConnectStatus = connect();
                        connectServerResult(mCurrentConnectStatus);
                    }
                    SystemClock.sleep(3000);
                }
            }
        }
    };

    public String GetInetAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IPAddress;
    }

    private void stopTcpClient() {
        if (mHeartBeat != null) {
            mHeartBeat.stop();
        }
        if (mTcpClient != null) {
            mTcpClient.close();
            mTcpClient = null;
        }
    }

    /**
     * 功能描述 : 设置设备id
     *
     * @param : deviceId ！= null
     */
    public void setDeviceId(@Nullable String deviceId) {
        if (!TextUtils.isEmpty(deviceId)) {
            mDeviceId = deviceId;
        }
    }

    /**
     * 功能描述 : 更新设备状态
     */
    public void updateDeviceStatus() {
        sendHeartbeat(mTcpClient, Command.HEARTBEAT, mDeviceId);
    }

    public void setTcpListener(@NonNull TcpListener tcpListener) {
        mTcpListener = tcpListener;
    }

    /**
     * 服务器连接的心跳包
     */
    private HeartBeat mHeartBeat;

    private void startHeartbeat() {
        if (mHeartBeat != null) {
            mHeartBeat = new HeartBeat();
            mHeartBeat.setListener(new HeartBeat.Listener() {
                @Override
                public void connectFailed() {
                    mCurrentConnectStatus = false;
                }

                @Override
                public void sendHeartbeat() {
                    updateDeviceStatus();
                }
            });
        }
        mHeartBeat.start();
    }


    /**
     * 功能描述 : 回传数据处理
     */
    private SocketListener mSoketListener = new SocketListener() {

        @Override
        public void onOpen(boolean isConnect) {
            Log.d("tcp connect", "" + isConnect);
            if (isConnect) {
                startHeartbeat();
            }
        }

        @Override
        public void onMessage(byte[] dataBuffer) {

        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onClose() {
        }
    };

    private int getWorkStatus() {
        if (mTcpListener == null) {
            return 0;
        }
        return mTcpListener.getWorkStatus();
    }

    /**
     * 获取二维码
     */
    private interface HttpListener {
        void finsh();
    }

    private int mRequestResult = 0;
    private Runnable mQrRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (mCurrentConnectStatus && isScanQrShow) {
                    synchronized (this) {
                        mRequestResult = 1;
                        startShowQrcode(new HttpListener() {

                            @Override
                            public void finsh() {
                                mRequestResult = 0;
                                notify();
                            }
                        });
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (mRequestResult != 0) {
                            if (mRequestResult > 0) {
                                mRequestResult++;
                            }
                            if (mRequestResult >= 16) {
                                break;
                            }
                            SystemClock.sleep(1000);
                        }

                        mRequestResult = 1;
                        initRandomcode(new HttpListener() {

                            @Override
                            public void finsh() {
                                mRequestResult = 0;
                            }

                        });

                        while (mRequestResult != 0) {
                            if (mRequestResult > 0) {
                                mRequestResult++;
                            }
                            if (mRequestResult >= 5) {
                                break;
                            }
                            SystemClock.sleep(1000);
                        }
                    }
                    SystemClock.sleep(2000);
                } else {
                    SystemClock.sleep(500);
                }
            }
        }
    };

    private void startShowQrcode(HttpListener listener) {
        if (checkUploadDeviceInforStatus()) {
            if (mNeedChangeQrcode) {
                getQrcodeOrRandomcode(mDeviceId, GetLoginType.QRCODE, listener);
            } else {
                if (mTcpListener != null && mTcpClient != null && mTcpClient.isConnecting()) {
                    mTcpListener.showQrcode(mQrcodeStr);
                }
                listener.finsh();
            }
        } else {
            uploadDeviceInformation(listener);
        }
    }

    private boolean isScanQrShow = false;

    public void setScanQrShowStatus(boolean isShow) {
        isScanQrShow = isShow;
    }

    private class GetLoginType {
        private static final String QRCODE = "qrcode";
        private static final String RANDOMCODE = "randomcode";
        private static final String ADVERTISMENT = "advertisement";
    }

    private void getQrcodeOrRandomcode(String deviceId, final String type, final HttpListener listener) {

    }

    /**
     * 初始化登录的随机码
     */
    private void initRandomcode(HttpListener listener) {
        if (mLoginPassword != null) {
            listener.finsh();
            return;
        }
        String randomcodeStr = PreferenceHelper.readString(mContext, Config.SETTING_CONFIG, "randomcode");
        if (randomcodeStr != null) {
            Randomcode randomcode = new Gson().fromJson(randomcodeStr, Randomcode.class);
            String recodeDate = randomcode.getDeadtime();
            long nowTime = System.currentTimeMillis();
            String nowDate = SysUtils.getDataTimeStr("yyyy-MM-dd", nowTime);
            String password = randomcode.getRandom_code();
            if (!nowDate.equals(recodeDate) || password == null || (password != null && password.isEmpty())) {
                getQrcodeOrRandomcode(mDeviceId, GetLoginType.RANDOMCODE, listener);
            } else {
                mLoginPassword = password;
                listener.finsh();
            }
        } else {
            getQrcodeOrRandomcode(mDeviceId, GetLoginType.RANDOMCODE, listener);
        }
    }

    public String getLoginPassword() {
        return mLoginPassword;
    }

    public class Command {
        public static final int HEARTBEAT = 1;
        public static final int OPEN_DEVICE = 2;
        public static final int SPORTS_PLAN = 3;
        public static final int LD_OPEN_DEVICE = 4;
    }

    public <T> Object getGsonToObject(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, cls);
    }

    private String getHeartbeatJson(int command, String deviceId) {
        HeartbeatPacket heartbeatPacket = new HeartbeatPacket(command, deviceId);
        int workStatus = getWorkStatus();
        heartbeatPacket.setWork_status(workStatus);
        Gson gson = new Gson();
        String str = gson.toJson(heartbeatPacket).toString();
        return str;
    }

    public void sendHeartbeat(TcpClient tcpClient, int command, String deviceId) {
        String jsonStr = getHeartbeatJson(command, deviceId);
        if (jsonStr != null && tcpClient != null) {
            tcpClient.sendData(jsonStr);
        }
    }

    private String getOpenDeviceAckJson(int command, String device_id, String user_id, String status) {
        OpenDeviceAck openDeviceAck = new OpenDeviceAck(command, device_id, user_id, status);
        Gson gson = new Gson();
        String str = gson.toJson(openDeviceAck).toString();
        return str;
    }

    public void sendOpenDeviceStatus(TcpClient tcpClient, int command, String device_id, String user_id, String
            status) {
        String jsonStr = getOpenDeviceAckJson(command, device_id, user_id, status);
        if (jsonStr != null && tcpClient != null) {
            tcpClient.sendData(jsonStr);
        }
    }
}
