package com.tapc.platform.ui.witget;

import android.content.ContentResolver;
import android.content.Context;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.entity.BluetoothConnectStatus;
import com.tapc.platform.entity.EventEntity;
import com.tapc.platform.jni.Driver;
import com.tapc.platform.library.common.SystemSettings;
import com.tapc.platform.library.data.TreadmillWorkout;
import com.tapc.platform.library.util.WorkoutEnum;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.NoActionModel;
import com.tapc.platform.ui.base.BaseSystemView;
import com.tapc.platform.ui.view.WorkoutOSD;
import com.tapc.platform.utils.CommonUtils;
import com.tapc.platform.utils.IntentUtils;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.RxjavaUtils;
import com.tapc.platform.utils.SoundCtlUtils;
import com.tapc.platform.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.tapc.platform.R.id.calorie;

public class MenuBar extends BaseSystemView implements Observer {
    @BindView(R.id.fan)
    ImageButton mFan;
    @BindView(R.id.osdIncline)
    WorkoutOSD mIncline;
    @BindView(R.id.osdSpeed)
    WorkoutOSD mSpeed;
    @BindView(R.id.osdTime)
    WorkoutOSD mTime;
    @BindView(R.id.osdHeart)
    WorkoutOSD mHeart;
    @BindView(R.id.osdDateTime)
    WorkoutOSD mDateTime;
    @BindView(R.id.osdGoal)
    TextView mGoalText;
    @BindView(R.id.osdProgress)
    ProgressBar mTarget;
    @BindView(R.id.workoutOsd)
    LinearLayout mLinearLayoutOSD;
    @BindView(R.id.connect_status_LinearLayout)
    LinearLayout mConnectStatuslny;
    @BindView(R.id.wifi_status)
    ImageView mWifiStatus;
    @BindView(R.id.bluetooth_status)
    ImageView mBluetoothStatus;

    private WorkOuting mWorkOuting;
    private int mFanLevel = 3;

    @Override
    protected int getLayoutResID() {
        return R.layout.dialog_menu_bar;
    }

    public MenuBar(Context context) {
        super(context);
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, WindowManager.LayoutParams.MATCH_PARENT, (int) getResources()
                .getDimension(R.dimen.menuBar), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        showRunInformation(false);
        setWifi(null);
        changeFanStatus();
        mWorkOuting = WorkOuting.getInstance();
        RxjavaUtils.interval(1, 1, TimeUnit.SECONDS, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                updateTime();
                NoActionModel.getInstance().increaseCount();
                checkHasPersonRunning();
            }
        }, null);
        mWorkOuting.subscribeObserverNotification(this);
    }

    /**
     * 功能描述 : 时间更新
     */
    private void updateTime() {
        ContentResolver c = mContext.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(c, android.provider.Settings.System
                .TIME_12_24);
        String sysTimeStr;
        long sysTime = System.currentTimeMillis();
        if (strTimeFormat != null) {
            if (strTimeFormat.equals("24")) {
                sysTimeStr = CommonUtils.getDataTimeStr("HH:mm", sysTime);
            } else {
                sysTimeStr = CommonUtils.getDataTimeStr("hh:mm", sysTime);
            }
        } else {
            sysTimeStr = CommonUtils.getDataTimeStr("hh:mm", sysTime);
        }
        String sysDateStr = CommonUtils.getDataTimeStr("yyyy/MM/dd", sysTime);
        mDateTime.setTitle(sysTimeStr);
        mDateTime.setValue(sysDateStr);
    }

    /**
     * 功能描述 : 无人检测功能
     */
    private int noPersonRunningCount;
    private static int NOPERSON_DELAYTIME_S = 120;

    public void checkHasPersonRunning() {
        if (!mWorkOuting.isRunning()) {
            return;
        }
        TreadmillWorkout workout = (TreadmillWorkout) WorkOuting.getInstance().getWorkout();
        if (workout != null) {
            //            int paceRate = workout.getPaceFlag();
            int paceRate = 0;
            if (paceRate == 0 && !WorkOuting.getInstance().isPausing()) {
                noPersonRunningCount++;
                if (noPersonRunningCount >= NOPERSON_DELAYTIME_S) {
                    WorkOuting.getInstance().stop();
                } else {
                    return;
                }
            }
        }
        noPersonRunningCount = 0;
    }


    /**
     * 功能描述 : 显示运动信息栏
     */
    public void showRunInformation(boolean visibility) {
        if (visibility) {
            mLinearLayoutOSD.setVisibility(View.VISIBLE);
            mConnectStatuslny.setVisibility(View.INVISIBLE);
        } else {
            mLinearLayoutOSD.setVisibility(View.INVISIBLE);
            mConnectStatuslny.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wifiStatusChange(NetworkInfo networkInfo) {
        setWifi(networkInfo);
    }

    private void setWifi(NetworkInfo networkInfo) {
        if (NetUtils.isConnected(mContext)) {
            mWifiStatus.setImageResource(R.drawable.ic_wifi_connect);
            mWifiStatus.setVisibility(View.VISIBLE);
        } else {
            mWifiStatus.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueStatusChange(BluetoothConnectStatus connectStatus) {
        if (connectStatus.isConnected()) {
            mBluetoothStatus.setImageResource(R.drawable.ic_blue_connect);
            mBluetoothStatus.setVisibility(View.VISIBLE);
        } else {
            mBluetoothStatus.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.volume)
    public void voiceOnClick(View v) {
        SoundCtlUtils.getInstance().openVolume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeFanStatus(EventEntity.ChangeFanStatus fanStatus) {
        changeFanStatus();
    }

    @OnClick(R.id.fan)
    public void changeFanStatus() {
        if (mFanLevel == 0) {
            mFanLevel = 3;
            mFan.setBackgroundResource(R.drawable.ic_fan_on);
        } else if (mFanLevel > 0) {
            mFanLevel = 0;
            mFan.setBackgroundResource(R.drawable.ic_fan_off);
        }
        //        TapcApp.getInstance().getController().setFanLevel(mFanLevel);
    }

    @OnClick(R.id.back)
    void backOnClick() {
        Driver.back();
    }

    @OnClick(R.id.home)
    void homeOnClick() {
        //        Driver.home();
        IntentUtils.home(mContext);
    }

    public void updateWorkout(TreadmillWorkout workout) {
        // incline
        double incline = workout.getIncline();
        mIncline.setValue(String.format("%.1f", incline));
        // speed
        double speed = workout.getSpeed();
        mSpeed.setValue(String.format("%.1f", speed));
        // time
        long time = workout.getTotalTime();
        mTime.setValue(String.format("%02d:%02d", time / 60, time % 60));
        // heart
        int heart = (int) workout.getHeart();
        mHeart.setValue(String.valueOf(heart));

        float goal = workout.getGoal();
        switch (workout.getWorkoutGoal()) {
            case TIME:
                if (workout.getWorkoutStage() == WorkoutEnum.WorkoutStage.WARMUP) {
                    // mGoalText.setTitleName(getString(R.string.warmUp_time), "min");
                } else if (workout.getWorkoutStage() == WorkoutEnum.WorkoutStage.COOLDOWN) {
                    if (SystemSettings.COOLDOWN_TIME > 0) {
                        mGoalText.setText(R.string.relax_time);
                    }
                } else {
                    mGoalText.setText(R.string.goal_sports);
                }
                int timeProgress = workout.getTime();
                mGoalText.setText(String.format("%d", (int) (goal / 60)));
                if (timeProgress != 0) {
                    mTarget.setMax((int) goal);
                    mTarget.setProgress(timeProgress);
                }
                break;
            case DISTANCE:
                mGoalText.setText(R.string.goal_sports);
                float distanceProgress = workout.getDistance();
                if (distanceProgress != 0) {
                    mTarget.setMax((int) (goal * 100));
                    mTarget.setProgress((int) (distanceProgress * 100));
                }
                break;
            case CALORIE:
                mGoalText.setText(R.string.goal_sports);
                float calorieProgress = workout.getCalorie();
                if (calorie != 0) {
                    mTarget.setMax((int) (goal * 100));
                    mTarget.setProgress((int) calorieProgress);
                }
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        WorkoutEnum.WorkoutUpdate workoutUpdate = (WorkoutEnum.WorkoutUpdate) arg;
        if (workoutUpdate != null) {
            TreadmillWorkout workout = (TreadmillWorkout) mWorkOuting.getWorkout();
            if (workout == null) {
                return;
            }
            switch (workoutUpdate) {
                case UI_UPDATE:
                    updateWorkout(workout);
                    break;
                case UI_LEFT:
                    mIncline.setValue(String.format("%.1f", workout.getIncline()));
                    break;
                case UI_RIGHT:
                    mSpeed.setValue(String.format("%.1f", workout.getSpeed()));
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mWorkOuting.unsubscribeObserverNotification(this);
    }
}