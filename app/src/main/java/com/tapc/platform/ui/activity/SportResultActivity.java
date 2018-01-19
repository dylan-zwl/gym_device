package com.tapc.platform.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.library.abstractset.WorkoutInfo;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;

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
                        uploadSportData();
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

    private void uploadSportData() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitHandler.removeCallbacksAndMessages(null);
        UserManageModel.getInstance().logout();
    }
}
