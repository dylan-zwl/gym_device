package com.tapc.platform.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class WorkoutCtlSetActivity extends BaseActivity {
    public static final String SPEED = "speed";
    public static final String INCLINE = "incline";
    @BindView(R.id.workout_ctl_seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.workout_ctl_title)
    TextView mValueText;
    private String mType;
    private int mValueMax = 0;
    private int mValueMin = 0;
    private int mStartValue = 0;
    private int mValue;
    private int mValueStep;
    private int mValuePoint = 1;

    public static void launch(Context c, String type, double starValue) {
        Intent i = new Intent(c, WorkoutCtlSetActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("type", type);
        i.putExtra("starValue", starValue);
        c.startActivity(i);
        ((Activity) c).overridePendingTransition(R.anim.in, R.anim.out);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_workout_ctl;
    }

    @Override
    protected void initView() {
        super.initView();
        mType = getIntent().getStringExtra("type");
        if (mType != null) {
            mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {

                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    if (progress >= mValueMin) {
                        mValue = progress;
                        if (mType.equals(SPEED)) {
                            mValueText.setText(getString(R.string.speed) + " " + FormatUtils.format(getString(R.string
                                    .workout_ctl_title), mValue / mValuePoint) + "." + (mValue % mValuePoint) + " " +
                                    "km/h");
                        } else if (mType.equals(INCLINE)) {
                            mValueText.setText(getString(R.string.incline) + " " + FormatUtils.format(getString(R.string
                                    .workout_ctl_title), mValue / mValuePoint) + " %");
                        }
                    } else {
                        mSeekBar.setProgress(mValueMin);
                    }
                }
            });

            if (mType.equals(SPEED)) {
                mValuePoint = 10;
                mValueMax = (int) (TreadmillSystemSettings.MAX_SPEED * mValuePoint);
                mValueMin = (int) (TreadmillSystemSettings.MIN_SPEED * mValuePoint);
                mValueStep = (int) (TreadmillSystemSettings.STEP_SPEED * mValuePoint);
                mStartValue = (int) (getIntent().getDoubleExtra("starValue", 0) * mValuePoint);
                mSeekBar.setMax(mValueMax);
                mSeekBar.setProgress(mStartValue);
            } else if (mType.equals(INCLINE)) {
                mValuePoint = 1;
                mValueMax = (int) (TreadmillSystemSettings.MAX_INCLINE);
                mValueMin = (int) (TreadmillSystemSettings.MIN_INCLINE);
                mValueStep = (int) (TreadmillSystemSettings.STEP_INCLINE);
                mStartValue = (int) (getIntent().getDoubleExtra("starValue", 0));
                mSeekBar.setMax(mValueMax);
                mSeekBar.setProgress(mStartValue);
            }
        } else {
            finish();
        }
    }

    @OnClick(R.id.confirm_btn)
    protected void confirm(View v) {
        if (WorkOuting.getInstance().isRunning()) {
            if (mType.equals(SPEED)) {
                WorkOuting.getInstance().onRightPanel(((float) mValue) / mValuePoint);
            } else if (mType.equals(INCLINE)) {
                WorkOuting.getInstance().onLeftPanel(((float) mValue) / mValuePoint);
            }
        }
        finish();
    }

    @OnClick(R.id.cancle_btn)
    protected void cancle(View v) {
        finish();
    }

    @OnClick(R.id.workout_ctl_add)
    protected void addTime(View v) {
        mSeekBar.setProgress(mSeekBar.getProgress() + mValueStep);
    }

    @OnClick(R.id.workout_ctl_sub)
    protected void subTime(View v) {
        if ((mSeekBar.getProgress() - mValueStep) >= mValueMin) {
            mSeekBar.setProgress(mSeekBar.getProgress() - mValueStep);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(R.anim.in, R.anim.out);
    }
}
