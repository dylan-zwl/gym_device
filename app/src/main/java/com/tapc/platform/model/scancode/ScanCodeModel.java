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
import com.tapc.platform.model.scancode.dao.response.ResponseDto;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.model.scancode.entity.UploadDeviceInfo;
import com.tapc.platform.model.tcp.SocketListener;
import com.tapc.platform.model.tcp.TcpClient;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.PreferenceHelper;

import java.lang.reflect.Type;

public class ScanCodeModel {
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
    private UploadDeviceInfo mUploadDeviceInfo;
    private ScanCodeHttpRequest mHttpRequest;

    public interface ScanCodeListener {
        void showQrcode(String qrcodeStr);

        void connectServerResult(boolean isSuccess);

        void openDevice(ScanCodeUser user);

        void recvSportPlan(ExerciseProgram exerciseProgram);

        int getWorkStatus();

        void seveLoginRandomcode(String randomcode);
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
        mListener.connectServerResult(isSuccess);
    }

    private boolean connect() {
        boolean isConnected = false;
        try {
            mListener.showQrcode(null);
            if (NetUtils.isConnected(mContext) && !TextUtils.isEmpty(mDeviceId)) {
                Log.d("tcp connect", "start  device id : " + mDeviceId);
                String ip = NetUtils.getInetAddress(ScanCodeUrl.BASE_SERVER_URL);
                if (TextUtils.isEmpty(ip)) {
                    ip = ScanCodeUrl.IP;
                }
                mTcpClient = new TcpClient();
                mManage.setClient(mTcpClient);
                mTcpClient.setListener(mSoketListener);
                isConnected = mTcpClient.connect(ip, ScanCodeUrl.PORT, 4000);
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
            mHttpRequest = ScanCodeHttpRequest.getInstance();
            mHttpRequest.init(deviceId);
        }
    }

    public ScanCodeHttpRequest getHttpRequest() {
        return mHttpRequest;
    }

    /**
     * 功能描述 : 根据不同机型设置设备参数
     *
     * @param deviceInfo
     */
    public void setUploadDeviceInfor(UploadDeviceInfo deviceInfo) {
        this.mUploadDeviceInfo = deviceInfo;
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
        mHeartBeat.start(10000);
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
                    ScanCodeUser user = mManage.getUser(command, jsonStr);
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
            try {
                //上传设备信息
                boolean isUploadDeviceInfo = PreferenceHelper.readBoolean(mContext, "", "is_upload_device_infor",
                        false);
                if (!isUploadDeviceInfo) {
                    ResponseDto response = null;
                    while (true) {
                        if (mCurrentConnectStatus) {
                            response = mHttpRequest.uploadDeviceInformation(mUploadDeviceInfo, ResponseDto.class);
                            if (response != null && (response.getStatus() == 0 || response.getStatus() == 5)) {
                                PreferenceHelper.write(mContext, "", "is_upload_device_infor", true);
                                break;
                            }
                        }
                        Thread.sleep(3000);
                    }
                }


//                mHttpRequest.getService().uploadStatus(mDeviceId, "0").subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        String ss = responseBody.string();
//                        Log.d("#####", ss);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });
//
//                mHttpRequest.getService().uploadFault(mDeviceId, "0", "02").subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        String ss = responseBody.string();
//                        Log.d("#####", ss);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });
//
//                mHttpRequest.getService().getInformation(mDeviceId, ScanCodeHttpRequest.GetInformationType.CONFIG)
//                        .subscribe(new Consumer<ResponseBody>() {
//                            @Override
//                            public void accept(ResponseBody responseBody) throws Exception {
//                                String ss = responseBody.string();
//                                Log.d("#####", ss);
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//
//                            }
//                        });
//
//                mHttpRequest.getService().getInformation(mDeviceId, ScanCodeHttpRequest.GetInformationType.COST)
//                        .subscribe(new Consumer<ResponseBody>() {
//                            @Override
//                            public void accept(ResponseBody responseBody) throws Exception {
//                                String ss = responseBody.string();
//                                Log.d("#####", ss);
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//
//                            }
//                        });

                // 获取并显示二维码
                Type type2 = new TypeToken<ResponseDto<QrCodeInfo>>() {
                }.getType();
                while (true) {
                    if (mNeedChangeQrcode) {
                        QrCodeInfo qrCodeInfo = mHttpRequest.getCode(ScanCodeHttpRequest.GetInformationType.QRCODE,
                                type2);
                        if (qrCodeInfo != null) {
                            mQrcodeStr = qrCodeInfo.getQrcode_url();
                            mListener.showQrcode(mQrcodeStr);
                            if (mQrcodeStr != null) {
                                mNeedChangeQrcode = false;
                                //获取验证码
                                getRandomCode();
                            }
                        }
                    } else {
                        if (mCurrentConnectStatus) {
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

        private void getRandomCode() {
            if (TextUtils.isEmpty(mLoginPassword)) {
                Type type = new TypeToken<ResponseDto<Randomcode>>() {
                }.getType();
                if (mCurrentConnectStatus) {
                    Randomcode randomcode = mHttpRequest.getCode(ScanCodeHttpRequest.GetInformationType
                            .RANDOMCODE, type);
                    if (randomcode != null) {
                        String randomcodeStr = randomcode.getRandom_code();
                        if (!TextUtils.isEmpty(randomcodeStr)) {
                            mLoginPassword = randomcodeStr;
                            mListener.seveLoginRandomcode(randomcodeStr);
                        }
                    }
                }
            }
        }
    };
}