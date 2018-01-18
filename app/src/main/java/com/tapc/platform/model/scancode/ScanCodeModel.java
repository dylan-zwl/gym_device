package com.tapc.platform.model.scancode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.scancode.api.ScanCodeUrl;
import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.QrCodeInfo;
import com.tapc.platform.model.scancode.dao.response.Randomcode;
import com.tapc.platform.model.scancode.dao.response.ResponseDTO;
import com.tapc.platform.model.scancode.dao.response.User;
import com.tapc.platform.model.tcp.SocketListener;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.PreferenceHelper;

import java.lang.reflect.Type;

public class ScanCodeModel {
    public static final String IP = "112.74.87.166";
    public static final int PORT = 1234;
    private Context mContext;
    private DeviceType mDeviceType;
    private String mDeviceId = "";
    private boolean isUploadDeviceInfor = false;
    private TcpClient mTcpClient;
    private String mQrcodeStr = "";
    private String mLoginPassword;
    private boolean mNeedChangeQrcode = true;
    private boolean mCurrentConnectStatus = false;
    private ScanCodeListener mListener;
    private CommunicationManage mManage;

    public interface ScanCodeListener {
        void showQrcode(String qrcodeStr);

        void connectServerResult(boolean isSuccess);

        void openDevice(User user);

        void recvSportPlan(ExerciseProgram exerciseProgram);

        int getWorkStatus();
    }

    public ScanCodeModel(Context context, DeviceType deviceType) {
        mContext = context;
        mDeviceType = deviceType;
    }

    /**
     * 功能描述 : 连接服务器
     */
    private Thread mConnectServerThread;
    private Thread mGetQrThread;

    public void connectServer() {
        mConnectServerThread = new Thread(mConnectServerRunnable);
        mGetQrThread = new Thread(mGetQrRunnable);
        mManage = new CommunicationManage(mDeviceId);

        mConnectServerThread.start();
        mGetQrThread.start();
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
        if (mListener != null) {
            mListener.connectServerResult(isSuccess);
        }
    }

    private boolean connect() {
        boolean isConnected = false;
        try {
            if (mListener != null) {
                mListener.showQrcode(null);
            }
            if (NetUtils.isConnected(mContext) && !TextUtils.isEmpty(mDeviceId)) {
                Log.d("tcp connect", "start  device id : " + mDeviceId);
                String ip = NetUtils.getInetAddress(ScanCodeUrl.BASE_SERVER);
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

    /**
     * 功能描述 : 更新设备状态
     */
    public void updateDeviceStatus() {
        mManage.sendHeartbeat(getWorkStatus());
    }

    public void setTcpListener(@NonNull ScanCodeListener tcpListener) {
        mListener = tcpListener;
    }

    /**
     * 服务器连接的心跳包
     */
    private HeartBeat mHeartBeat;

    private void startHeartbeat() {
        if (mHeartBeat == null) {
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
            String jsonStr = new String(dataBuffer);
            int command = mManage.getCommand(jsonStr);
            switch (command) {
                case CommunicationManage.Command.HEARTBEAT:
                    mHeartBeat.resetCount();
                    break;
                case CommunicationManage.Command.OPEN_DEVICE:
                case CommunicationManage.Command.THIRD_OPEN_DEVICE:
                    User user = mManage.getUser(command, jsonStr);
                    if (user != null) {
                        mListener.openDevice(user);
                        mNeedChangeQrcode = true;
                    }
                    break;
                case CommunicationManage.Command.SPORTS_PLAN:
                    ExerciseProgram exerciseProgram = mManage.getExerciseProgram(jsonStr);
                    if (exerciseProgram != null) {
                        mListener.recvSportPlan(exerciseProgram);
                    }
                    break;
                default:
                    Log.d("tcp", "command error");
                    break;
            }
        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onClose() {
        }
    };

    private int getWorkStatus() {
        if (mListener == null) {
            return 0;
        }
        return mListener.getWorkStatus();
    }

    /**
     * 功能描述 : 登录验证码
     */
    public String getLoginPassword() {
        return mLoginPassword;
    }

    /**
     * 获取并显示二维码
     */
    private Runnable mGetQrRunnable = new Runnable() {
        @Override
        public void run() {
            HttpRequest httpRequest = new HttpRequest(mDeviceId);
            try {
                //上传设备信息
                boolean isUploadDeviceInfo = PreferenceHelper.readBoolean(mContext, "", "is_upload_device_infor",
                        false);
                if (!isUploadDeviceInfo) {
                    ResponseDTO response = null;
                    while (true) {
                        response = httpRequest.uploadDeviceInformation("", "", "", ResponseDTO.class);
                        PreferenceHelper.write(mContext, "", "is_upload_device_infor", true);
                        if (response != null && (response.getStatus() == 0 || response.getStatus() == 5)) {
                            break;
                        }
                        Thread.sleep(3000);
                    }
                }

                //获取验证码
                Type type = new TypeToken<ResponseDTO<Randomcode>>() {
                }.getType();
                while (true) {
                    Randomcode randomcode = httpRequest.getCode(HttpRequest.GetLoginType.RANDOMCODE, type);
                    if (randomcode != null) {
                        String randomcodeStr = randomcode.getRandom_code();
                        if (!TextUtils.isEmpty(randomcodeStr)) {
                            mLoginPassword = randomcodeStr;
                            break;
                        }
                    }
                }

                //获取并显示二维码
                Type type2 = new TypeToken<ResponseDTO<QrCodeInfo>>() {
                }.getType();
                while (true) {
                    if (mNeedChangeQrcode) {
                        QrCodeInfo qrCodeInfo = httpRequest.getCode(HttpRequest.GetLoginType.QRCODE, type2);
                        if (qrCodeInfo != null) {
                            mQrcodeStr = qrCodeInfo.getQrcode_url();
                            mListener.showQrcode(mQrcodeStr);
                            mNeedChangeQrcode = false;
                        }
                    } else {
                        if (mListener != null && mTcpClient != null && mTcpClient.isConnecting()) {
                            mListener.showQrcode(mQrcodeStr);
                        }
                    }
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("", "");
            }
        }
    };
}