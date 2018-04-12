package com.tapc.platform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.library.abstractset.WorkoutInfo;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.scancode.ScanCodeHttpRequest;
import com.tapc.platform.model.scancode.dao.request.TreadmillStageSportData;
import com.tapc.platform.model.scancode.dao.request.UserSportsData;
import com.tapc.platform.model.scancode.dao.response.ScanCodeUser;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

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
        userSportsData.setTime(String.valueOf(time));
        userSportsData.setDistance(String.valueOf(distance));
        userSportsData.setCalorie(String.valueOf(calories));
        userSportsData.setPlan_id("0");
        userSportsData.setSport_type("0");

        userSportsData.setDevice_id(user.getDeviceId());
        userSportsData.setUser_id(user.getUserId());
        userSportsData.setScan_order_id(user.getScan_order_id());

        ScanCodeHttpRequest.getInstance().getService().uploadSportsData(userSportsData).subscribeOn(Schedulers.io())
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
//        List<WorkoutIntervalInfo> mWorkoutInfoIntervalList = mEnginImpl.mWorkouting.getWorkoutInterval()
// .mIntervalList;
//        List<TreadmillStageSportData> TreadmillStageSportDatas = new ArrayList<TreadmillStageSportData>();
//        if (mWorkoutInfoIntervalList == null || mWorkoutInfoIntervalList.isEmpty()) {
//            TreadmillStageSportData spData = new TreadmillStageSportData();
//            spData.setTime(String.valueOf(workoutInfo.getTime()));
//            spData.setIncline(String.valueOf(SysUtils.formatDouble(1, workoutInfo.getIncline())));
//            spData.setSpeed(String.valueOf(SysUtils.formatDouble(1, workoutInfo.getSpeed())));
//            TreadmillStageSportDatas.add(spData);
//        } else {
//            for (WorkoutIntervalInfo infor : mWorkoutInfoIntervalList) {
//                TreadmillStageSportData spData = new TreadmillStageSportData();
//                spData.setTime(String.valueOf(infor.getTime()));
//                spData.setIncline(String.valueOf(SysUtils.formatDouble(1, infor.getIncline())));
//                spData.setSpeed(String.valueOf(SysUtils.formatDouble(1, infor.getSpeed())));
//                TreadmillStageSportDatas.add(spData);
//            }
//        }

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
