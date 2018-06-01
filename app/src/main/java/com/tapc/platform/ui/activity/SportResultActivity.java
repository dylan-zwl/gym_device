package com.tapc.platform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.application.Config;
import com.tapc.platform.library.abstractset.WorkoutInfo;
import com.tapc.platform.library.data.BikeWorkoutInfo;
import com.tapc.platform.library.data.TreadmillWorkoutInfo;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.scancode.ScanCodeHttpRequest;
import com.tapc.platform.model.scancode.dao.request.BikeStageSportData;
import com.tapc.platform.model.scancode.dao.request.TreadmillStageSportData;
import com.tapc.platform.model.scancode.dao.request.UserSportsData;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SportResultActivity extends BaseActivity {
    @BindView(R.id.result_distance)
    TextView mTextDistance;
    @BindView(R.id.result_time)
    TextView mTextTime;
    @BindView(R.id.result_speed)
    TextView mTextSpeed;
    @BindView(R.id.result_calorie)
    TextView mTextCalorie;

    public static final String TYPE_STOP = "stop";
    public static final String TYPE_NOMAL = "nomal";
    private String mShowType;
    private Handler mHandler;
    private Handler mExitHandler;
    private int mCount = 0;

    public static void launch(Context c, @NonNull String type) {
        Intent i = new Intent(c, SportResultActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("type", type);
        c.startActivity(i);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_sportresult;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView() {
        mShowType = this.getIntent().getExtras().getString("type");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                WorkoutInfo workoutInfo = WorkOuting.getInstance().getWorkoutInfo();
                if (workoutInfo != null) {
                    initDataShow(workoutInfo);
                    if (mShowType.equals(TYPE_STOP)) {
                        upLoadTreadmillStageSportData(workoutInfo);
                    }
                } else {
                    mCount++;
                    if (mCount <= 10) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 500);

        mExitHandler = new Handler();
        mExitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 30 * 1000);
    }

    private void initDataShow(WorkoutInfo workoutInfo) {
        long time = workoutInfo.getTime();
        float distance = workoutInfo.getDistance();
        String timeStr = FormatUtils.format("%02d:%02d:%02d", time / 3600, time % 3600 / 60, time % 60);
        mTextTime.setText(timeStr);
        mTextCalorie.setText(FormatUtils.format("%.2f", workoutInfo.getCalorie()));
        mTextDistance.setText(FormatUtils.format("%.2f", distance));
        mTextSpeed.setText(FormatUtils.format("%.1f", distance * 3600 / time));
    }

    private void upLoadTreadmillStageSportData(WorkoutInfo workoutInfo) {
        int distance = (int) (workoutInfo.getDistance() * 1000);
        int calories = (int) (workoutInfo.getCalorie() * 1000);
        int time = (int) (workoutInfo.getTime());

        ScanCodeUser user = (ScanCodeUser) UserManageModel.getInstance().getScanCodeUser();

        UserSportsData userSportsData = new UserSportsData<TreadmillStageSportData>();
        userSportsData.setTime(time);
        userSportsData.setDistance(distance);
        userSportsData.setCalorie(calories);

        userSportsData.setPlan_id(user.getPlanId());
        userSportsData.setDevice_id(user.getDeviceId());
        userSportsData.setUser_id(user.getUserId());
        userSportsData.setScan_order_id(user.getScan_order_id());

//        setSportData(userSportsData, workoutInfo);

        ScanCodeHttpRequest.getInstance().uploadSportsData(userSportsData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Exception {
                String reslult = responseBody.string();
                Log.d("######", reslult);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });


    }

    private void setSportData(UserSportsData userSportsData, WorkoutInfo workoutInfo) {
        List<WorkoutInfo> mWorkoutInfoIntervalList = (List<WorkoutInfo>) WorkOuting.getInstance()
                .getWorkoutInterval();
        switch (Config.DEVICE_TYPE) {
            case TREADMILL:
                List<Object> treadmillStageSportDatas = new ArrayList<Object>();
                if (mWorkoutInfoIntervalList == null || mWorkoutInfoIntervalList.isEmpty()) {
                    TreadmillWorkoutInfo info = (TreadmillWorkoutInfo) workoutInfo;
                    TreadmillStageSportData data = new TreadmillStageSportData();
                    data.setTime(String.valueOf(workoutInfo.getTime()));
                    data.setIncline(String.valueOf(info.getIncline()));
                    data.setSpeed(String.valueOf(info.getSpeed()));
                    treadmillStageSportDatas.add(data);
                } else {
                    for (WorkoutInfo infor : mWorkoutInfoIntervalList) {
                        TreadmillWorkoutInfo info = (TreadmillWorkoutInfo) infor;
                        TreadmillStageSportData data = new TreadmillStageSportData();
                        data.setTime(String.valueOf(infor.getTime()));
                        data.setIncline(String.valueOf(info.getIncline()));
                        data.setSpeed(String.valueOf(info.getSpeed()));
                        treadmillStageSportDatas.add(data);
                    }
                }
                userSportsData.setSport_data(treadmillStageSportDatas);
                break;
            case BIKE:
                List<Object> bikeStageSportDatas = new ArrayList<Object>();
                if (mWorkoutInfoIntervalList == null || mWorkoutInfoIntervalList.isEmpty()) {
                    BikeWorkoutInfo info = (BikeWorkoutInfo) workoutInfo;
                    BikeStageSportData data = new BikeStageSportData();
                    data.setTime(String.valueOf(workoutInfo.getTime()));
                    data.setResistance(String.valueOf(info.getResistance()));
                    data.setWatt(String.valueOf(info.getWatt()));
//                    spData.setSpeed(String.valueOf(info.getSpeed()));
                    bikeStageSportDatas.add(data);
                } else {
                    for (WorkoutInfo infor : mWorkoutInfoIntervalList) {
                        BikeWorkoutInfo info = (BikeWorkoutInfo) infor;
                        BikeStageSportData data = new BikeStageSportData();
                        data.setTime(String.valueOf(infor.getTime()));
                        data.setResistance(String.valueOf(info.getResistance()));
                        data.setWatt(String.valueOf(info.getWatt()));
//                        spData.setSpeed(String.valueOf(info.getSpeed()));
                        bikeStageSportDatas.add(data);
                    }
                }
                userSportsData.setSport_data(bikeStageSportDatas);
                break;
            case POWER:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserManageModel.getInstance().logout();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitHandler.removeCallbacksAndMessages(null);
    }
}
