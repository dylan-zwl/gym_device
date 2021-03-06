package com.tapc.platform.model.healthcat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.tapc.platform.model.scancode.HeartBeat;
import com.tapc.platform.model.tcp.SocketListener;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.NetUtils;

public abstract class BaseCtlModel {
    public static final String BASE_SERVER_URL = "beta-accsail.healthmall.cn";
    public static final String IP = "121.201.63.252";
    public static final int PORT = 50890;

    protected Context mContext;
    protected String mDeviceId = "";
    protected TcpClient mTcpClient;
    protected boolean mCurrentConnectStatus = false;
    protected BaseListener mBaseListener;
    private BaseCommunicationManage mManage;
    protected int mHeatbeatTime = 10000;

    public interface BaseListener {
        Object serverReadInfo();

        void showQrcode(String qrcodeStr);

        void connectServerResult(boolean isSuccess);

        void login(boolean isSuccess);

        boolean serverSetRunStatus(boolean isStart);

        boolean serverSetLock(boolean lock);
    }

    public BaseCtlModel(Context context) {
        mContext = context;
        mManage = getManage();
    }

    /**
     * 功能描述 : 连接服务器
     */
    private Thread mConnectServerThread;
    private Thread mGetQrThread;

    public void connectServer() {
        mConnectServerThread = new Thread(mConnectServerRunnable);
        mConnectServerThread.start();
    }

    public void disConnectServer() {
        stopTcpClient();
        if (mHeartBeat != null) {
            mHeartBeat.stop();
        }
        if (mConnectServerThread != null) {
            mConnectServerThread.interrupt();
            mConnectServerThread = null;
        }
        if (mGetQrThread != null) {
            mGetQrThread.interrupt();
            mGetQrThread = null;
        }
    }

    private void connectServerResult(boolean isSuccess) {
        if (!isSuccess) {
            stopTcpClient();
        }
        mBaseListener.connectServerResult(isSuccess);
    }

    private boolean connect() {
        boolean isConnected = false;
        try {
            mBaseListener.showQrcode(null);
            if (NetUtils.isConnected(mContext) && !TextUtils.isEmpty(mDeviceId)) {
                Log.d("tcp connect", "start  device id : " + mDeviceId);
                String ip = NetUtils.getInetAddress(BASE_SERVER_URL);
                if (TextUtils.isEmpty(ip)) {
                    ip = IP;
                }
                mTcpClient = new TcpClient();
                mTcpClient.setListener(mSoketListener);
                if (mManage != null) {
                    mManage.setDeviceId(mDeviceId);
                    mManage.setTcpClient(mTcpClient);
                }
                isConnected = mTcpClient.connect(ip, PORT, 4000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    public boolean getConnectStatus() {
        return mCurrentConnectStatus;
    }

    /**
     * 连接，重连服务器线程
     */
    private Runnable mConnectServerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        if (mCurrentConnectStatus == false) {
                            stopTcpClient();
                            mCurrentConnectStatus = connect();
                            connectServerResult(mCurrentConnectStatus);
                        }
                        Thread.sleep(3000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

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

    public String getDeviceId() {
        return mDeviceId;
    }

    /**
     * 功能描述 : 更新设备状态
     */
    public abstract void sendHeartbeat();

    /**
     * 服务器连接的心跳包
     */
    protected HeartBeat mHeartBeat;

    //心跳包相隔时间
    public void setHeatbeatTime(int heatbeatTime) {
        this.mHeatbeatTime = heatbeatTime;
    }

    protected void startHeartbeat() {
        if (mHeartBeat == null) {
            mHeartBeat = new HeartBeat();
            mHeartBeat.setListener(new HeartBeat.Listener() {
                @Override
                public void connectFailed() {
                    mCurrentConnectStatus = false;
                }

                @Override
                public void sendHeartbeat() {
                    BaseCtlModel.this.sendHeartbeat();
                }
            });
        }
        mHeartBeat.start(mHeatbeatTime);
    }


    /**
     * 功能描述 : tcp 连接状态监听和接收到数据的处理
     */
    protected abstract void recvMessage(byte[] dataBuffer);

    private SocketListener mSoketListener = new SocketListener() {

        @Override
        public void onOpen(boolean isConnect) {
            Log.d("tcp connect", "" + isConnect);
        }

        @Override
        public void onMessage(byte[] dataBuffer) {
            recvMessage(dataBuffer);
        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onClose() {
        }
    };

    public abstract BaseCommunicationManage getManage();

    //    public abstract BaseListener getBaseListener();
    protected void setBaseListener(BaseListener baseListener) {
        this.mBaseListener = baseListener;
    }
//
//    public void setManage(BaseCommunicationManage manage) {
//        this.mManage = manage;
//    }

    public void setRunStatus(byte cmd, byte[] data) {
        byte runStatus = mManage.getDataPack().getCommanData(data, 0);
        boolean setRunStatusResult = false;
        if (runStatus != -1) {
            switch (runStatus) {
                case AckStatus.START:
                    setRunStatusResult = mBaseListener.serverSetRunStatus(true);
                    break;
                case AckStatus.STOP:
                    setRunStatusResult = mBaseListener.serverSetRunStatus(false);
                    break;
            }
        }
        if (setRunStatusResult) {
            mManage.ackStatus(cmd, AckStatus.SUCCESS);
        } else {
            mManage.ackStatus(cmd, AckStatus.FAIL);
        }
    }

    public void setLock(byte cmd, byte[] data) {
        byte lock = mManage.getDataPack().getCommanData(data, 0);
        boolean lockResult = false;
        if (lock != -1) {
            switch (lock) {
                case AckStatus.LOCK:
                    lockResult = mBaseListener.serverSetLock(true);
                    break;
                case AckStatus.UNLOCK:
                    lockResult = mBaseListener.serverSetLock(false);
                    break;
            }
        }
        if (lockResult) {
            mManage.ackStatus(cmd, AckStatus.SUCCESS);
        } else {
            mManage.ackStatus(cmd, AckStatus.FAIL);
        }
    }
}