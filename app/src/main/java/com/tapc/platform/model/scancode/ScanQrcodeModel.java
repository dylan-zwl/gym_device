package com.tapc.platform.model.scancode;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.scancode.dao.response.QrCodeInfo;
import com.tapc.platform.model.scancode.dao.response.Randomcode;
import com.tapc.platform.model.scancode.dao.response.ResponseDTO;
import com.tapc.platform.model.scancode.dao.response.SportsMenu;
import com.tapc.platform.model.scancode.dao.response.User;
import com.tapc.platform.model.scancode.api.ScanCodeUrl;
import com.tapc.platform.model.tcp.SocketListener;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.PreferenceHelper;

import java.net.InetAddress;

public class ScanQrcodeModel {
    public static final String IP = "112.74.87.166";
    public static final int PORT = 1234;
    private Context mContext;
    private DeviceType mDeviceType;
    private String mDeviceId = "";
    private boolean isUploadDeviceInfor = false;
    private TcpClient mTcpClient;
    private String mQrcodeStr = "";
    private String mLoginPassword;
    public static boolean mNeedChangeQrcode = true;
    private boolean mCurrentConnectStatus = false;
    private TcpListener mTcpListener;
    private CommunicationManage mManage;

    public interface TcpListener {
        void showQrcode(String qrcodeStr);

        void connectServerResult(boolean isSuccess);

        void openDevice(User user);

        void recvSportPlan(String userId, SportsMenu plan_load);

        int getWorkStatus();
    }

    public ScanQrcodeModel(Context context, DeviceType deviceType) {
        mContext = context;
        mDeviceType = deviceType;
    }

    /**
     * 功能描述 : 连接服务器
     */
    public void connectServer() {
        new Thread(mRunnable).start();
        new Thread(mQrRunnable).start();
        mManage = new CommunicationManage(mDeviceId);
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
                mManage.setClient(mTcpClient);
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
        mManage.sendHeartbeat(getWorkStatus());
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
     * 功能描述 : tcp 连接状态监听和接收到数据的处理
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
     * 获取并显示二维码
     */
    private Runnable mQrRunnable = new Runnable() {
        @Override
        public void run() {
            HttpRequest httpRequest = new HttpRequest(mDeviceId);

            //上传设备信息
            boolean isUploadDeviceInfo = PreferenceHelper.readBoolean(mContext, "", "is_upload_device_infor", false);
            if (!isUploadDeviceInfo) {
                ResponseDTO response = null;
                while (true) {
                    response = httpRequest.uploadDeviceInformation("", "", "", ResponseDTO.class);
                    PreferenceHelper.write(mContext, "", "is_upload_device_infor", true);
                    if (response != null && (response.getStatus() == 0 || response.getStatus() == 5)) {
                        break;
                    }
                    SystemClock.sleep(3000);
                }
            }

            //获取验证码
            while (true) {
                Randomcode randomcode = httpRequest.getCode(HttpRequest.GetLoginType.QRCODE, Randomcode.class);
                if (randomcode != null) {
                    String randomcodeStr = randomcode.getRandom_code();
                    if (!TextUtils.isEmpty(randomcodeStr)) {
                        mLoginPassword = randomcodeStr;
                        break;
                    }
                }
            }

            //获取并显示二维码
            while (true) {
                if (mNeedChangeQrcode) {
                    QrCodeInfo qrCodeInfo = httpRequest.getCode(HttpRequest.GetLoginType.QRCODE, QrCodeInfo.class);
                    if (qrCodeInfo != null) {
                        mQrcodeStr = qrCodeInfo.getQrcode_url();
                        mNeedChangeQrcode = false;
                    }
                } else {
                    if (mTcpListener != null && mTcpClient != null && mTcpClient.isConnecting()) {
                        mTcpListener.showQrcode(mQrcodeStr);
                    }
                }
                SystemClock.sleep(3000);
            }
        }
    };
}
