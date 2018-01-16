package com.tapc.platform.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.tapc.platform.R;
import com.tapc.platform.broadcast.send.WorkoutBroadcase;
import com.tapc.platform.entity.DeviceWorkout;
import com.tapc.platform.entity.EventEntity;
import com.tapc.platform.jni.Driver;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.NoActionModel;
import com.tapc.platform.model.key.KeyCode;
import com.tapc.platform.model.key.KeyModel;
import com.tapc.platform.ui.witget.CountdownDialog;
import com.tapc.platform.ui.witget.ErrorDialog;
import com.tapc.platform.ui.witget.MenuBar;
import com.tapc.platform.ui.witget.NumberDialog;
import com.tapc.platform.utils.IntentUtils;
import com.tapc.platform.utils.SoundCtlUtils;

import org.greenrobot.eventbus.EventBus;

import static com.tapc.platform.library.common.SystemSettings.mContext;

public class MenuService extends Service {
    private LocalBinder mBinder;
    private MenuBar mMenuBar;
    private CountdownDialog mCountdownDialog;
    private ErrorDialog mErrorDialog;
    private WorkOuting mWorkOuting = WorkOuting.getInstance();
    private KeyModel mKeyModel;
    private NumberDialog mNumberDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBinder = new LocalBinder(this);
        initView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("sevice", "MenuService onDestroy");
        if (mMenuBar != null) {
            mMenuBar.dismiss();
            mMenuBar = null;
        }
        if (mKeyModel != null) {
            mKeyModel.stopListen();
        }
        if (mNumberDialog != null) {
            mNumberDialog.dismiss();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("sevice", "MenuService onBind");
        return mBinder;
    }

    private void initView() {
        initKeyEvent();

        //初始化dialog
        mMenuBar = new MenuBar(this);
        mMenuBar.show();

        mNumberDialog = new NumberDialog(this);
        initCountdownDialog();
        initErrorDialog();
    }

    public MenuBar getMenuBar() {
        return mMenuBar;
    }

    public CountdownDialog getCountdownDialog() {
        return mCountdownDialog;
    }

    public ErrorDialog getmErrorDialog() {
        return mErrorDialog;
    }

    private void initCountdownDialog() {
        mCountdownDialog = new CountdownDialog(this);
        mCountdownDialog.setFinishedListener(new CountdownDialog.FinishedListener() {
            @Override
            public void onFinished() {
                if (mWorkOuting.isRunning()) {
                    WorkoutBroadcase.send(mContext, DeviceWorkout.RESUME);
                } else {
                    WorkoutBroadcase.send(mContext, DeviceWorkout.START);
                }
            }
        });
    }


    private void initErrorDialog() {
        mErrorDialog = new ErrorDialog(this);
        mErrorDialog.setListener(new ErrorDialog.Listener() {
            @Override
            public void show() {
                if (WorkOuting.getInstance().isRunning()) {
                    mErrorDialog.dismiss();
                    WorkoutBroadcase.send(mContext, DeviceWorkout.STOP);
                }
            }

            @Override
            public void hide() {

            }
        });
    }

    /**
     * 功能描述 : 物理按键功能模块
     */
    private static final int NULL_NUM = -1;
    private int mPressnumber = NULL_NUM;

    private void clickWarningSound() {
        SoundCtlUtils.getInstance().playBeep(mContext, R.raw.notify);
    }

    private void clickSound() {
        SoundCtlUtils.getInstance().clickSound(mContext);
    }

    private void initKeyEvent() {
        mKeyModel = new KeyModel();
        mKeyModel.startListen(new KeyModel.KeyListener() {
            @Override
            public void receverMcuKey(int keycode) {
                NoActionModel.getInstance().cleanNoActionCount();

                if (mErrorDialog.isShown() || mCountdownDialog.isShown()) {
                    clickWarningSound();
                    return;
                }

                switch (keycode) {
                    case KeyCode.START:
                        if (!mWorkOuting.isRunning()) {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.COUNTDDOWN);
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.STOP:
                        if (mWorkOuting.isRunning()) {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.STOP);
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.PAUSE:
                        if (mWorkOuting.isRunning() && !mWorkOuting.isPausing()) {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.PAUSE);
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.START_PAUSE:
                        if (!mWorkOuting.isRunning()) {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.COUNTDDOWN);
                        } else {
                            if (mWorkOuting.isPausing()) {
                                WorkoutBroadcase.send(mContext, DeviceWorkout.RESUME);
                            } else {
                                WorkoutBroadcase.send(mContext, DeviceWorkout.PAUSE);
                            }
                        }
                        clickSound();
                        break;
                    case KeyCode.START_STOP:
                        if (!mWorkOuting.isRunning()) {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.COUNTDDOWN);
                        } else {
                            WorkoutBroadcase.send(mContext, DeviceWorkout.STOP);
                        }
                        clickSound();
                        break;
                    case KeyCode.SPEED_INC_KEY:
                        if (mWorkOuting.isRunning()) {
                            if (mWorkOuting.getWorkout().getSpeed() < TreadmillSystemSettings.MAX_SPEED) {
                                mWorkOuting.onRightKeypadAdd();
                                clickSound();
                            }
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.SPEED_DEC_KEY:
                        if (mWorkOuting.isRunning()) {
                            if (mWorkOuting.getWorkout().getSpeed() > TreadmillSystemSettings.MIN_SPEED) {
                                mWorkOuting.onRightKeypadSub();
                                clickSound();
                            }
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.INCLINE_INC:
                        if (mWorkOuting.isRunning()) {
                            if (mWorkOuting.getWorkout().getSpeed() < TreadmillSystemSettings.MAX_INCLINE) {
                                mWorkOuting.onLeftKeypadAdd();
                                clickSound();
                            }
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.INCLINE_DEC:
                        if (mWorkOuting.isRunning()) {
                            if (mWorkOuting.getWorkout().getSpeed() > TreadmillSystemSettings.MIN_INCLINE) {
                                mWorkOuting.onRightKeypadSub();
                                clickSound();
                            }
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.SET_SPEED:
                        if (mWorkOuting.isRunning() && mPressnumber != NULL_NUM) {
                            if (mPressnumber >= TreadmillSystemSettings.MIN_SPEED && mPressnumber <=
                                    TreadmillSystemSettings.MAX_SPEED) {
                                mWorkOuting.onRightPanel(mPressnumber);
                                mNumberDialog.setVisivility(false);
                            } else {
                                mNumberDialog.setMsgShow(R.string.keyvalue_error);
                                clickWarningSound();
                            }
                            mPressnumber = NULL_NUM;
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.SET_INCLINE:
                        if (mWorkOuting.isRunning() && mPressnumber != NULL_NUM) {
                            if (mPressnumber >= TreadmillSystemSettings.MIN_INCLINE && mPressnumber <=
                                    TreadmillSystemSettings.MAX_INCLINE) {
                                mWorkOuting.onLeftPanel(mPressnumber);
                                mNumberDialog.setVisivility(false);
                            } else {
                                mNumberDialog.setMsgShow(R.string.keyvalue_error);
                                clickWarningSound();
                            }
                            mPressnumber = NULL_NUM;
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.VOLUME_EN_DIS:
                        if (SoundCtlUtils.getInstance().isEnble()) {
                            SoundCtlUtils.getInstance().setSoundEnble(false);
                        } else {
                            SoundCtlUtils.getInstance().setSoundEnble(true);
                        }
                        clickSound();
                        break;
                    case KeyCode.VOLUME_EN:
                        SoundCtlUtils.getInstance().openVolume();
                        clickSound();
                        break;
                    case KeyCode.VOLUME_INC:
                        if (SoundCtlUtils.getInstance().getVolume() < SoundCtlUtils.getInstance().getMaxVolume()) {
                            clickSound();
                        }
                        SoundCtlUtils.getInstance().addVolume();
                        break;
                    case KeyCode.VOLUME_DEC:
                        clickSound();
                        SoundCtlUtils.getInstance().subVolume();
                        break;
                    case KeyCode.HOME:
                        IntentUtils.home(mContext);
                        clickSound();
                        break;
                    case KeyCode.BACK:
                        Driver.back();
                        clickSound();
                        break;
                    case KeyCode.FAN:
                        EventBus.getDefault().register(new EventEntity.ChangeFanStatus());
                        clickSound();
                        break;
                    case KeyCode.PROGRAM_SET:
                        if (!mWorkOuting.isRunning()) {
                            clickSound();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.RELAX:
                        if (mWorkOuting.isRunning()) {
                            clickSound();
                            mWorkOuting.coolDown();
                        } else {
                            clickWarningSound();
                        }
                        break;
                    case KeyCode.MEDIA_PREVIOUS:
                        Driver.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                        clickSound();
                        break;
                    case KeyCode.MEDIA_NEXT:
                        Driver.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
                        clickSound();
                        break;
                    default:
                        if (keycode >= KeyCode.NUM_MIN && keycode <= KeyCode.NUM_MAX) {
                            mPressnumber = mNumberDialog.getPressNum(keycode - KeyCode.NUM_MIN);
                        }
                        if (keycode >= KeyCode.SPEED_MIN && keycode <= KeyCode.SPEED_MAX) {
                            setSpeed(keycode - KeyCode.SPEED_MIN);
                        }
                        if (keycode >= KeyCode.INCLINE_MIN && keycode <= KeyCode.INCLINE_MAX) {
                            setIncline(keycode - KeyCode.INCLINE_MIN);
                        }
                        break;
                }
            }
        });
    }

    private void setSpeed(float speed) {
        if (mWorkOuting.isRunning()) {
            mWorkOuting.onRightPanel(speed);
            clickSound();
        } else {
            clickWarningSound();
        }
    }

    private void setIncline(float incline) {
        if (mWorkOuting.isRunning()) {
            mWorkOuting.onLeftPanel(incline);
            clickSound();
        } else {
            clickWarningSound();
        }
    }
}