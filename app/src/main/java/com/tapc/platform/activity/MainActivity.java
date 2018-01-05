package com.tapc.platform.activity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tapc.platform.R;
import com.tapc.platform.entity.FixedAppEntity;
import com.tapc.platform.library.controller.MachineController;
import com.tapc.platform.model.common.ClickModel;
import com.tapc.platform.service.MenuService;
import com.tapc.platform.ui.activity.ApplicationActivity;
import com.tapc.platform.ui.activity.GoalActivity;
import com.tapc.platform.ui.activity.HelpActivity;
import com.tapc.platform.ui.activity.LanguageAcivity;
import com.tapc.platform.ui.activity.SettingActivity;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.ui.view.WorkoutCtrl;
import com.tapc.platform.ui.view.WorkoutGoal;
import com.tapc.platform.ui.view.WorkoutText;
import com.tapc.platform.utils.IntentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/5.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.screen1)
    LinearLayout mMain1View1;
    @BindView(R.id.screen2)
    LinearLayout mMain1View2;
    @BindView(R.id.main_stop_ll)
    LinearLayout mStopll;
    @BindView(R.id.main_running_ll)
    LinearLayout mRunningll;
    @BindView(R.id.inclineCtrl)
    WorkoutCtrl mInclineCtrl;
    @BindView(R.id.speedCtrl)
    WorkoutCtrl mSpeedCtrl;
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
    @BindView(R.id.restartButton)
    Button mRestart;
    @BindView(R.id.loginButton)
    Button mLoginShowText;

    public static class ReloadApp {
    }

    private ClickModel mClickModel;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        initEnterSetting();
    }

    @OnClick({R.id.internetButton, R.id.weatherButton, R.id.gameButton, R.id.bluetoothButton,
            R.id.datetimeButton, R.id.app, R.id.wifiButton, R.id.newsButton, R.id.storeButton, R
            .id.videoButton, R.id.musicButton, R.id.languageButton, R.id.exerciseButton, R.id.reportButton, R.id
            .vaButton, R.id.helpButton, R.id.registerButton, R.id.loginButton, R.id.nextButton, R.id.preButton, R.id
            .system_settings, R.id.goalDistanceButton, R.id.goalTimeButton, R.id.goalCalorieButton})
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
//                if (isDevieRun()) {
//                    Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
//                } else {
//                    IntentUtils.startActivity(this, ProgramAcitvity.class);
//                }
                break;
            case R.id.reportButton:
//                if (isDevieRun()) {
//                    Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
//                } else {
//                    SportResultActivity.launch(this, "click_show");
//                }
                break;
            case R.id.vaButton:
//                if (isDevieRun() && TapcApp.getInstance().getVaRecordPosition().isNeedToResume()) {
//                    IntentUtils.startActivity(this, ScenePlayActivity.class);
//                } else {
//                    IntentUtils.startActivity(this, SceneRunActivity.class);
//                }
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
        switch (page) {
            case 1:
            default:
                mMain1View1.setVisibility(View.VISIBLE);
                mMain1View2.setVisibility(View.INVISIBLE);
//                if (isDevieRun()) {
                mTapcApp.getService().getMenuBar().showRunInformation(false);
//                }
                break;
            case 2:
                mMain1View1.setVisibility(View.INVISIBLE);
                mMain1View2.setVisibility(View.VISIBLE);
//                if (isDevieRun()) {
                mTapcApp.getService().getMenuBar().showRunInformation(true);
//                }
                break;
        }
    }

    private void initEnterSetting() {
        mClickModel = new ClickModel.Builder().onceClickTimeout(1000).maxClickNumbers(5)
                .listener(new ClickModel.Listener() {
                    @Override
                    public void onClickCompleted() {
                        IntentUtils.startActivity(mContext, SettingActivity.class);
                    }
                }).create();
    }

    private void startThirdApp(Context mContext, String packageName, String className) {
        boolean result = IntentUtils.startThirdApp(mContext, packageName, className, null, Intent
                .FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Logger.d("start third app result : " + result);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 100)
    public void reload(ReloadApp reload) {
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
}
