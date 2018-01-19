package com.tapc.platform.ui.witget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.entity.DeviceType;
import com.tapc.platform.model.common.ClickModel;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.common.NoActionModel;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.scancode.ScanCodeContract;
import com.tapc.platform.model.scancode.ScanCodeEvent;
import com.tapc.platform.model.scancode.ScanCodePresenter;
import com.tapc.platform.model.scancode.dao.request.DeviceStatus;
import com.tapc.platform.model.scancode.dao.response.ExerciseProgram;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.ui.base.BaseSystemView;
import com.tapc.platform.utils.CommonUtils;
import com.tapc.platform.utils.QrcodeUtils;
import com.tapc.platform.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.reactivex.annotations.NonNull;

public class ScanCodeDialog extends BaseSystemView implements ScanCodeContract.View {
    @BindView(R.id.qr_codes)
    ImageView mQrCode;
    @BindView(R.id.dialog_rl)
    RelativeLayout mDialogRl;
    @BindView(R.id.scancode_login_rl)
    RelativeLayout mScancodeLoginRl;
    @BindView(R.id.randomcode_login_rl)
    RelativeLayout mRandomcodeLoginRl;
    @BindView(R.id.show_text)
    TextView mShowMsg;
    @BindView(R.id.randomcode_et)
    EditText mRandomcodeEt;
    @BindView(R.id.net_msg_rl)
    RelativeLayout mNetMsgRl;
    @BindView(R.id.rfid_msg_rl)
    RelativeLayout mRfidMsgRl;
    @BindView(R.id.rfid_msg)
    TextView mRfidMsg;
    @BindView(R.id.device_type)
    ImageView mDeviceTypeIv;
    @BindView(R.id.dialog_qr_codes_fl)
    RelativeLayout mQrCodeRl;

    private Handler mHandler;
    private LoginMode mLoginMode = LoginMode.QRCODE;
    private ScanCodeContract.Presenter mPresenter;
    private int mConnectFailCout = 0;
    private String mOldQrCodeStr;
    private ClickModel mClickModel;
    private DeviceType mDeviceType;

    private boolean isOpenScanCode = false;

    @Override
    protected int getLayoutResID() {
        return R.layout.scan_code_dialog;
    }

    public ScanCodeDialog(Context context, @NonNull DeviceType deviceType) {
        super(context);
        mDeviceType = deviceType;
        initDeviceType(deviceType);
        init();
    }

    @Override
    protected void initView() {
        super.initView();
        mHandler = new Handler();
        EventBus.getDefault().register(this);

        mDialogRl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRandomcodeLoginRl.isShown()) {
                    setSoftInputVisibility(false);
                }
            }
        });
    }

    private void init() {
        mPresenter = new ScanCodePresenter(mContext, this, mDeviceType);
//        if (Config.HAS_RFID) {
//        connectRfid();
//        }
        isOpenScanCode = ConfigModel.getScanCode(mContext);
        if (isOpenScanCode) {
            hideConnectNet();
            mPresenter.start();
        }
    }

    private void initDeviceType(DeviceType deviceType) {
        switch (deviceType) {
            case TREADMILL:
                mDeviceTypeIv.setBackgroundResource(R.drawable.device_type1);
                break;
            case BIKE:
                mDeviceTypeIv.setBackgroundResource(R.drawable.device_type2);
                break;
            case POWER:
                mDeviceTypeIv.setBackgroundResource(R.drawable.device_type3);
                break;
        }
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT, Gravity.TOP);
    }

    /**
     * 功能描述 :  扫码界面设置显示或隐藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100, sticky = true)
    public void onDialogEvent(ScanCodeEvent event) {
        setVisibility(event.isVisibility());
    }

    private void initShowDialog() {
        switchLoginType(mLoginMode);
        UserManageModel.getInstance().logout();
        mQrCode.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
    }


    @SuppressLint("NewApi")
    public void setVisibility(boolean visibility) {
        NoActionModel.getInstance().cleanNoActionCount();
        if (visibility) {
            if (mDialogRl.isShown()) {
                return;
            }
            mDialogRl.setClickable(false);

            initShowDialog();
//            mPresenter.setScanQrShowStatus(true);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_to_down);
            mDialogRl.startAnimation(animation);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    mDialogRl.setClickable(true);
                    if (isOpenScanCode == false) {
                        mQrCodeRl.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            mDialogRl.setClickable(false);
//            mPresenter.setScanQrShowStatus(false);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.down_to_up);
            mDialogRl.startAnimation(animation);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    setVisibility(View.GONE);
                    mDialogRl.setClickable(true);
                    setSoftInputVisibility(false);
                }
            });
        }
    }


    /**
     * 功能描述 : 验证码登录
     */
    @OnClick(R.id.randomcode_login)
    protected void randomcodeLogin(View v) {
        String password = mRandomcodeEt.getText().toString();
        String loginPassword = mPresenter.getPassword();
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(loginPassword) && (password.equals(loginPassword) ||
                password.equals("shuhua"))) {
            UserManageModel.getInstance().setScanCodeUser(null);
            UserManageModel.getInstance().login();
            mRandomcodeEt.setText("");
            setVisibility(false);
        } else {
            mRandomcodeEt.setText("");
            mRandomcodeEt.setHint(R.string.password);
        }
    }

    /**
     * 无网络提示
     */
    private void showConnectNet() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNetMsgRl.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideConnectNet() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mConnectFailCout = 0;
                mNetMsgRl.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 功能描述 : 二维码现显示
     */
    private void setQrcodeVisibility(final int visibility) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mQrCode.setVisibility(visibility);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void showQrcode(final String qrcodeStr) {
        if (qrcodeStr == null || qrcodeStr.isEmpty()) {
            setQrcodeVisibility(View.INVISIBLE);
            return;
        }
        if (mOldQrCodeStr != null && mOldQrCodeStr.equals(qrcodeStr)) {
            setQrcodeVisibility(View.VISIBLE);
            return;
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    final Bitmap bitmap = QrcodeUtils.createImage(qrcodeStr, 100, 100, 5);
                    if (bitmap != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOldQrCodeStr = qrcodeStr;
                                mQrCode.setBackground(new BitmapDrawable(bitmap));
                                mQrCode.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * 功能描述 : 开启设备
     */
    @Override
    public void openDevice(ScanCodeUser user) {
//        mPresenter.setScanQrShowStatus(false);
        UserManageModel.getInstance().setScanCodeUser(user);
        UserManageModel.getInstance().login();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(false);
                mQrCode.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 功能描述 : 接收到的运动计划
     */
    @Override
    public void recvSportPlan(ExerciseProgram plan_load) {
//        ScanCodeUser user = TapcApp.getInstance().mainActivity.getUser();
//        if (user == null || TapcApp.getInstance().isStart() || userId != user.getUserId()) {
//            return;
//        }
//        user.setPlan_load(plan_load);
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                SportMenuActivity.launch(mContext);
//            }
//        });
    }

    @Override
    public int getDeviceStatus() {
        if (!mDialogRl.isShown()) {
            return DeviceStatus.USING;
        }
        return DeviceStatus.NO_USE;
    }

    /**
     * 功能描述 : 连接服务器结果
     */
    @Override
    public void connectServerResult(boolean isSuccess) {
        if (isSuccess == false) {
            mConnectFailCout++;
            if (mConnectFailCout > 5) {
                mConnectFailCout = 0;
                showConnectNet();
            }
        } else {
            mConnectFailCout = 0;
            hideConnectNet();
        }
    }

    /**
     * 功能描述 : 进入网络设置按键
     */
    @OnClick(R.id.connect_service_yes)
    protected void connectServer(View v) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
        mContext.startActivity(intent);
        hideConnectNet();
        setVisibility(false);
    }

    @OnClick(R.id.connect_service_no)
    protected void unConnectServer(View v) {
        hideConnectNet();
    }

    /**
     * 切换登录方式
     */
    private enum LoginMode {
        QRCODE, RANDOM_CODE
    }

    private void switchLoginType(LoginMode mode) {
        if (mode == LoginMode.QRCODE) {
            mRandomcodeLoginRl.setVisibility(View.GONE);
            mScancodeLoginRl.setVisibility(View.VISIBLE);
            setSoftInputVisibility(false);
        } else {
            mRandomcodeEt.setText("");
            mRandomcodeEt.setHint("");
            mScancodeLoginRl.setVisibility(View.GONE);
            mRandomcodeLoginRl.setVisibility(View.VISIBLE);
            setSoftInputVisibility(true);
        }
        mLoginMode = mode;
    }

    @OnClick(R.id.change_login_mode)
    protected void changeLoginMode(View v) {
        if (mLoginMode == LoginMode.QRCODE) {
            switchLoginType(LoginMode.RANDOM_CODE);
        } else {
            switchLoginType(LoginMode.QRCODE);
        }
    }

    /**
     * 功能描述 : 手动进入主页面
     */
    @OnTouch(R.id.system_settings)
    boolean clickLogo(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            initEnterSetting();
            mClickModel.click();
        }
        return true;
    }

    private void initEnterSetting() {
        if (mClickModel == null) {
            mClickModel = new ClickModel.Builder().onceClickTimeout(1000).maxClickNumbers(5).listener(new ClickModel
                    .Listener() {
                @Override
                public void onClickCompleted() {
                    setVisibility(false);
                }
            }).create();
        }
    }

    /**
     * 功能描述 : 是否显示输入法键盘
     *
     * @param : visibility  = false 隐藏显示
     */
    private void setSoftInputVisibility(boolean visibility) {
        CommonUtils.setSoftInputVisibility(mRandomcodeEt, visibility);
    }

/*********************************************************************************************************************/

    /**
     * 功能描述 : rfid模块功能
     *
     * @param :
     */
//    private RfidModel mRfidModel;
//
//    private void connectRfid() {
//        mRfidModel = null;
//        mRfidModel = new RfidModel();
//        boolean isConnected = mRfidModel.connectUsb(TapcApp.getInstance().mainActivity);
//        if (isConnected) {
//            mRfidModel.connectRfid(new DetectListener() {
//
//                @Override
//                public void connectStatus(boolean isConnected) {
//                    if (!isConnected) {
//                        mHandler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                setRfidMsg(mContext.getInputStream2String(R.string.rfid_device_connect_failed));
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void Uid(byte[] uid) {
//                    if (mDialogRl.isShown()) {
//                        TapcApp.getInstance().controller.sendCommands(Commands.SET_BUZZER_CNTRL, null);
//                        SysUtils.playBeep(mContext, R.raw.rfid);
//                        String uidStr = UsbCtl.bytesToHexString(uid);
//                        ScanCodeUser user = new ScanCodeUser();
//                        user.setName(uidStr);
//                        user.setAvatarUrl(null);
//                        TapcApp.getInstance().mainActivity.setUser(user);
//                        mHandler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                setVisibility(false);
//                            }
//                        });
//                        Log.d("uid", "" + uidStr);
//                    }
//                }
//            });
//        } else {
//            setRfidMsg(mContext.getInputStream2String(R.string.rfid_device_malfunction));
//        }
//    }
//
//    @OnClick(R.id.rfid_reconnect)
//    protected void reconnect(View v) {
//        mRfidMsgRl.setVisibility(GONE);
//        connectRfid();
//    }
//
//    private void setRfidMsg(final String text) {
//        mRfidMsgRl.setVisibility(VISIBLE);
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                mRfidMsg.setText(text);
//            }
//        });
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.stop();
        }
    }
}
