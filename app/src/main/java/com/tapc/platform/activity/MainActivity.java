package com.tapc.platform.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tapc.platform.R;
import com.tapc.platform.broadcast.send.WorkoutBroadcase;
import com.tapc.platform.entity.DeviceWorkout;
import com.tapc.platform.entity.EventEntity;
import com.tapc.platform.entity.FixedAppEntity;
import com.tapc.platform.library.abstractset.ProgramSetting;
import com.tapc.platform.library.common.SystemSettings;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.controller.MachineController;
import com.tapc.platform.library.data.TreadmillProgramSetting;
import com.tapc.platform.library.data.TreadmillWorkout;
import com.tapc.platform.library.util.WorkoutEnum;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.ClickModel;
import com.tapc.platform.model.common.NoActionModel;
import com.tapc.platform.model.vaplayer.VaRecordPosition;
import com.tapc.platform.service.MenuService;
import com.tapc.platform.ui.activity.ApplicationActivity;
import com.tapc.platform.ui.activity.GoalActivity;
import com.tapc.platform.ui.activity.HelpActivity;
import com.tapc.platform.ui.activity.LanguageAcivity;
import com.tapc.platform.ui.activity.ProgramAcitvity;
import com.tapc.platform.ui.activity.ScenePlayActivity;
import com.tapc.platform.ui.activity.SceneRunActivity;
import com.tapc.platform.ui.activity.SettingActivity;
import com.tapc.platform.ui.activity.SportResultActivity;
import com.tapc.platform.ui.activity.WorkoutCtlSetActivity;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.ui.view.DeviceCtl;
import com.tapc.platform.ui.view.WorkoutGoal;
import com.tapc.platform.ui.view.WorkoutText;
import com.tapc.platform.ui.witget.CountdownDialog;
import com.tapc.platform.ui.witget.MenuBar;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.IntentUtils;
import com.tapc.platform.utils.SoundCtlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tapc.platform.entity.DeviceWorkout.COUNTDDOWN;

/**
 * Created by Administrator on 2018/1/5.
 */

public class MainActivity extends BaseActivity implements Observer {
    @BindView(R.id.screen1)
    LinearLayout mMain1View1;
    @BindView(R.id.screen2)
    LinearLayout mMain1View2;
    @BindView(R.id.main_stop_ll)
    LinearLayout mStopll;
    @BindView(R.id.main_running_ll)
    LinearLayout mRunningll;
    @BindView(R.id.inclineCtrl)
    DeviceCtl mLeftDeviceCtl;
    @BindView(R.id.speedCtrl)
    DeviceCtl mRightDeviceCtl;
    @BindView(R.id.workout_goal)
    WorkoutGoal mWorkoutGoal;
    @BindView(R.id.totalTime)
    TextView mGoalRun;
    @BindView(R.id.time)
    WorkoutText mWorkoutTextTime;
    @BindView(R.id.distance)
    WorkoutText mWorkoutTextDistance;
    @BindView(R.id.calorie)
    WorkoutText mWorkoutTextCalorie;
    @BindView(R.id.heart)
    WorkoutText mWorkoutTextHeart;
    @BindView(R.id.altitude)
    WorkoutText mWorkoutTextPace;
    @BindView(R.id.pauseButton)
    Button mPause;
    @BindView(R.id.resumeButton)
    Button mRestart;
    @BindView(R.id.loginButton)
    Button mLoginShowText;

    private ClickModel mClickModel;
    private WorkOuting mWorkOuting = WorkOuting.getInstance();
    private ProgramSetting mProgramSetting;
    private boolean isAppToBackground;
    private int mPage = 0;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        mWorkOuting.subscribeObserverNotification(this);
        EventBus.getDefault().register(this);

        initEnterSetting();
        initDeviceCtl();
        initWorkOutReceive();
        initNoActionModel();
    }

    private void initDeviceCtl() {
        mLeftDeviceCtl.setConfig(TreadmillSystemSettings.MIN_INCLINE, TreadmillSystemSettings.MAX_INCLINE,
                TreadmillSystemSettings.STEP_INCLINE, 0.0f, 500);
        mLeftDeviceCtl.setOnClickListener(new DeviceCtl.Listener() {
            @Override
            public void onAddClick() {
                mLeftDeviceCtl.setValue(mWorkOuting.onLeftKeypadAdd());
            }

            @Override
            public void onSubClick() {
                mLeftDeviceCtl.setValue(mWorkOuting.onLeftKeypadSub());
            }

            @Override
            public void onValueTextClick(float value) {
                WorkoutCtlSetActivity.launch(mContext, WorkoutCtlSetActivity.INCLINE, value);
            }

            @Override
            public void setDeviceValue(float value) {
                mWorkOuting.onLeftPanel(value);
            }

        });

        mRightDeviceCtl.setConfig(TreadmillSystemSettings.MIN_SPEED, TreadmillSystemSettings.MAX_SPEED,
                TreadmillSystemSettings.STEP_SPEED, 0.0f, 100);
        mRightDeviceCtl.setOnClickListener(new DeviceCtl.Listener() {
            @Override
            public void onAddClick() {
                mRightDeviceCtl.setValue(mWorkOuting.onRightKeypadAdd());
            }

            @Override
            public void onSubClick() {
                mRightDeviceCtl.setValue(mWorkOuting.onRightKeypadSub());
            }

            @Override
            public void onValueTextClick(float value) {
                WorkoutCtlSetActivity.launch(mContext, WorkoutCtlSetActivity.SPEED, value);
            }

            @Override
            public void setDeviceValue(float value) {
                mWorkOuting.onRightPanel(value);
            }

        });
    }

    private void startCountdown() {
        if (!AppUtils.isApplicationBroughtToBackground(MainActivity.this)) {
            if (!IntentUtils.isTopActivity(mContext, ScenePlayActivity.class)) {
                IntentUtils.home(mContext);
                showMainPage(1);
            }
        }
        //通知下控预开始
        MachineController.getInstance().registerPreStart();
    }

    public void startRun() {
        showRunningLayout(true);
        //        TapcApp.getInstance().getVaRecordPosition().setVaRecordPosition(false, 0, -1);
    }

    public void pauseRun() {
        mRestart.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.INVISIBLE);
    }

    public void resumeRun() {
        mRestart.setVisibility(View.INVISIBLE);
        mPause.setVisibility(View.VISIBLE);
    }

    public void stopRun() {
        showRunningLayout(false);
        SoundCtlUtils.getInstance().playBeep(mContext, R.raw.beeps);
        if (!AppUtils.isApplicationBroughtToBackground(MainActivity.this)) {
            IntentUtils.home(mContext);
            showMainPage(1);
        }
        SportResultActivity.launch(this, "stoprun_show");
        mProgramSetting = null;
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
                    mLeftDeviceCtl.setValue(workout.getIncline());
                    break;
                case UI_RIGHT:
                    mRightDeviceCtl.setValue(workout.getSpeed());
                    break;
                case UI_START:
                    startRun();
                    break;
                case UI_PAUSE:
                    pauseRun();
                    break;
                case UI_RESUME:
                    resumeRun();
                    break;
                case UI_STOP:
                    stopRun();
                    break;
            }
        }
    }

    private void updateWorkout(TreadmillWorkout workout) {
        // time
        long time = workout.getTotalTime();
        mWorkoutTextTime.setValue(String.format("%02d:%02d", time / 60, time % 60));

        // distance
        double distance = workout.getTotalDistance();
        mWorkoutTextDistance.setValue(String.format("%.2f", distance));

        // calorie
        double calorie = workout.getTotalCalorie();
        mWorkoutTextCalorie.setValue(String.format("%.2f", calorie));

        // heartRate
        mWorkoutTextHeart.setValue(String.valueOf(workout.getHeart()));

        // Altitude
        double altitude = workout.getTotalAltitude();
        mWorkoutTextPace.setValue(String.format("%.2f", altitude));

        float goal = workout.getGoal();
        switch (workout.getWorkoutGoal()) {
            case TIME:
                if (workout.getWorkoutStage() == WorkoutEnum.WorkoutStage.WARMUP) {
                    // mWorkoutGoal.setTitleName(getString(R.string.warmUp_time), "min");
                } else if (workout.getWorkoutStage() == WorkoutEnum.WorkoutStage.COOLDOWN) {
                    if (SystemSettings.COOLDOWN_TIME > 0) {
                        mWorkoutGoal.setTitleName(getString(R.string.relax_time), "min");
                    }
                } else {
                    mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "min");
                }
                int timeProgress = workout.getTime();
                mGoalRun.setText(String.format("%d", (int) (goal / 60)));
                if (timeProgress != 0) {
                    mWorkoutGoal.SetRange(0, (int) goal);
                    mWorkoutGoal.setPos(timeProgress);
                }
                break;
            case DISTANCE:
                mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "km");
                float distanceProgress = workout.getDistance();
                mGoalRun.setText(String.format("%.0f", goal));
                if (distanceProgress != 0) {
                    mWorkoutGoal.SetRange(0, (int) (goal * 100));
                    mWorkoutGoal.setPos(distanceProgress * 100);
                }
                break;
            case CALORIE:
                mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "kcal");
                float calorieProgress = workout.getCalorie();
                mGoalRun.setText(String.format("%.0f", goal));
                if (calorie != 0) {
                    mWorkoutGoal.SetRange(0, (int) (goal * 100));
                    mWorkoutGoal.setPos(calorieProgress);
                }
                break;
        }
    }

    private void showRunningLayout(boolean isRunning) {
        MenuBar menuBar = mTapcApp.getService().getMenuBar();
        if (menuBar == null) {
            return;
        }
        if (isRunning) {
            mRunningll.setVisibility(View.INVISIBLE);
            mStopll.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.VISIBLE);
            mRestart.setVisibility(View.INVISIBLE);
            menuBar.showRunInformation(false);
        } else {
            mStopll.setVisibility(View.INVISIBLE);
            mRunningll.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.VISIBLE);
            mRestart.setVisibility(View.INVISIBLE);
            if (mMain1View2.isShown()) {
                menuBar.showRunInformation(true);
            } else {
                menuBar.showRunInformation(false);
            }
        }
    }

    @OnClick({R.id.startButton, R.id.pauseButton, R.id.resumeButton, R.id.stopButton})
    protected void deviceStatusOnclick(View v) {
        v.setClickable(false);
        switch (v.getId()) {
            case R.id.startButton:
                WorkoutBroadcase.send(mContext, COUNTDDOWN);
                break;
            case R.id.pauseButton:
                WorkoutBroadcase.send(mContext, DeviceWorkout.PAUSE);
                break;
            case R.id.resumeButton:
                WorkoutBroadcase.send(mContext, DeviceWorkout.COUNTDDOWN);
                break;
            case R.id.stopButton:
                WorkoutBroadcase.send(mContext, DeviceWorkout.STOP);
                break;
            default:
                break;
        }
        v.setClickable(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void otherActivityStar(ProgramSetting programSetting) {
        mProgramSetting = programSetting;
        WorkoutBroadcase.send(this, DeviceWorkout.COUNTDDOWN);
    }

    private void startWorkOuting() {
//        mProgramSetting = (TreadmillProgramSetting) TapcApplication.getInstance().getProgramSetting();
        if (mProgramSetting == null) {
            WorkoutEnum.ProgramType programType = WorkoutEnum.ProgramType.NORMAL;
            programType.setGoal(120 * 60);
            TreadmillProgramSetting treadmillProgramSetting = new TreadmillProgramSetting(programType);
            treadmillProgramSetting.setSpeed(TreadmillSystemSettings.MIN_SPEED);
            treadmillProgramSetting.setIncline(TreadmillSystemSettings.MIN_INCLINE);
            mProgramSetting = treadmillProgramSetting;
        }
        mWorkOuting.setProgramSetting(mProgramSetting);
        WorkOuting.getInstance().start();
    }

    public void initWorkOutReceive() {
        IntentUtils.registerReceiver(this, mWorkoutReceiver, DeviceWorkout.ACTION);
    }

    private BroadcastReceiver mWorkoutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DeviceWorkout.ACTION)) {
                int msgWhat = intent.getIntExtra(DeviceWorkout.MSG_WHAT, 0);
                switch (msgWhat) {
                    case COUNTDDOWN:
//                        if (!mWorkOuting.isRunning()) {
                        CountdownDialog countdownDialog = mTapcApp.getService().getCountdownDialog();
                        if (!countdownDialog.isShown()) {
                            startCountdown();
                            countdownDialog.show();
                        }
//                        }
                        break;
                    case DeviceWorkout.START:
                        startWorkOuting();
                        startRun();
                        break;
                    case DeviceWorkout.STOP:
                        WorkOuting.getInstance().stop();
                        break;
                    case DeviceWorkout.PAUSE:
                        WorkOuting.getInstance().pause();
                        break;
                    case DeviceWorkout.RESUME:
                        WorkOuting.getInstance().resume();
                        break;
                    default:
                        break;
                }
            }
        }
    };


    @OnClick({R.id.internetButton, R.id.weatherButton, R.id.gameButton, R.id.bluetoothButton, R.id.datetimeButton, R
            .id.app, R.id.wifiButton, R.id.newsButton, R.id.storeButton, R.id.videoButton, R.id.musicButton, R.id
            .languageButton, R.id.exerciseButton, R.id.reportButton, R.id.vaButton, R.id.helpButton, R.id
            .registerButton, R.id.loginButton, R.id.nextButton, R.id.preButton, R.id.system_settings, R.id
            .goalDistanceButton, R.id.goalTimeButton, R.id.goalCalorieButton})
    void startActivityOnClick(View v) {
        switch (v.getId()) {
            /* 第三方应用 */
            case R.id.internetButton:
                startThirdApp(mContext, FixedAppEntity.INTERNET.packageName, FixedAppEntity.INTERNET.className);
                break;
            case R.id.weatherButton:
                startThirdApp(mContext, FixedAppEntity.WEATHER.packageName, FixedAppEntity.WEATHER.className);
                break;
            case R.id.gameButton:
                startThirdApp(mContext, FixedAppEntity.GAME.packageName, FixedAppEntity.GAME.className);
                break;
            case R.id.storeButton:
                startThirdApp(mContext, FixedAppEntity.STORE.packageName, FixedAppEntity.STORE.className);
                break;
            case R.id.videoButton:
                startThirdApp(mContext, FixedAppEntity.VIDEO.packageName, FixedAppEntity.VIDEO.className);
                break;
            case R.id.musicButton:
                startThirdApp(mContext, FixedAppEntity.MUSIC.packageName, FixedAppEntity.MUSIC.className);
                break;
            case R.id.newsButton:
                startThirdApp(mContext, FixedAppEntity.NEWS.packageName, FixedAppEntity.NEWS.className);
                break;
            case R.id.bluetoothButton:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.wifiButton:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.datetimeButton:
                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                break;
            case R.id.app:
                IntentUtils.startActivity(this, ApplicationActivity.class);
                break;

            /* 各类功能 */
            case R.id.languageButton:
                IntentUtils.startActivity(this, LanguageAcivity.class);
                break;
            case R.id.exerciseButton:
                if (mWorkOuting.isRunning()) {
                    Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
                } else {
                    IntentUtils.startActivity(this, ProgramAcitvity.class);
                }
                break;
            case R.id.reportButton:
                if (mWorkOuting.isRunning()) {
                    Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
                } else {
                    SportResultActivity.launch(this, "click_show");
                }
                break;
            case R.id.vaButton:
                VaRecordPosition vaRecordPosition = ScenePlayActivity.getVaRecordPosition();
                if (mWorkOuting.isRunning() && vaRecordPosition != null && vaRecordPosition.isNeedToResume()) {
                    IntentUtils.startActivity(this, ScenePlayActivity.class);
                } else {
                    IntentUtils.startActivity(this, SceneRunActivity.class);
                }
                break;
            case R.id.helpButton:
                IntentUtils.startActivity(this, HelpActivity.class);
                break;

            case R.id.goalDistanceButton:
                GoalActivity.launch(this, GoalActivity.GoalModeType.DISTANCE);
                break;
            case R.id.goalTimeButton:
                GoalActivity.launch(this, GoalActivity.GoalModeType.TIME);
                break;
            case R.id.goalCalorieButton:
                GoalActivity.launch(this, GoalActivity.GoalModeType.CALORIE);
                break;

            /* 其他 */
            case R.id.system_settings:
                mClickModel.click();
                break;
            case R.id.nextButton:
                showMainPage(2);
                break;
            case R.id.preButton:
                showMainPage(1);
                break;
            default:
                break;
        }
    }

    private void showMainPage(int page) {
        mPage = page;
        switch (page) {
            case 1:
            default:
                mMain1View1.setVisibility(View.VISIBLE);
                mMain1View2.setVisibility(View.INVISIBLE);
                if (mWorkOuting.isRunning()) {
                    mTapcApp.getService().getMenuBar().showRunInformation(false);
                }
                break;
            case 2:
                mMain1View1.setVisibility(View.INVISIBLE);
                mMain1View2.setVisibility(View.VISIBLE);
                if (mWorkOuting.isRunning()) {
                    mTapcApp.getService().getMenuBar().showRunInformation(true);
                }
                break;
        }
    }

    private void initEnterSetting() {
        mClickModel = new ClickModel.Builder().onceClickTimeout(1000).maxClickNumbers(5).listener(new ClickModel
                .Listener() {
            @Override
            public void onClickCompleted() {
                IntentUtils.startActivity(mContext, SettingActivity.class);
            }
        }).create();
    }

    private void startThirdApp(Context context, String packageName, String className) {
        //        boolean result = IntentUtils.startThirdApp(mContext, packageName, className, null, Intent
        //                .FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //        Logger.d("start third app result : " + result);
        ApplicationActivity.launch(context, ApplicationActivity.START_APP, packageName, className);
    }

    private void initNoActionModel() {
        NoActionModel.getInstance().setListener(new NoActionModel.Listener() {
            @Override
            public boolean restriction() {
                if (mTapcApp.getService().getCountdownDialog().isShown() || WorkOuting.getInstance().isRunning() ||
                        !mTapcApp.isScreenOn()) {
                    return false;
                }
                if (isAppToBackground && AppUtils.isApplicationBroughtToBackground(mContext)) {
                    return false;
                }
                return true;
            }

            @Override
            public void count(int total) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 100)
    public void reload(EventEntity.ReloadApp reload) {
        IntentUtils.stopService(this, MenuService.class);
        MachineController.getInstance().stop();
        finish();
        IntentUtils.startActivity(this, MainActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TOP);
        System.exit(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showMainPage(1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppToBackground = false;
        if (mPage == 2) {
            mTapcApp.getService().getMenuBar().showRunInformation(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppToBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWorkoutReceiver != null) {
            IntentUtils.unregisterReceiver(this, mWorkoutReceiver);
            mWorkoutReceiver = null;
        }
        mWorkOuting.unsubscribeObserverNotification(this);
        EventBus.getDefault().unregister(this);
    }
}
