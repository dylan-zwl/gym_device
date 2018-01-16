package com.tapc.platform.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.data.TreadmillProgramSetting;
import com.tapc.platform.library.util.WorkoutEnum;
import com.tapc.platform.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dylan on 2018/1/7.
 */

public class SetProgramTimeAcitvity extends BaseActivity {
    @BindView(R.id.program_time_seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.program_time_title)
    TextView mProgramTimeText;

    private TreadmillProgramSetting mProgramSetting;
    private int mProgramTimeMax = 99;
    private int mProgramTimeMin = 5;
    private int mStartTime = 30;
    private int mProgramTime;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_program_time;
    }

    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getIntent().getExtras();
        int program = 0;
        if (bundle != null) {
            program = bundle.getInt("program");
        }
        mProgramSetting = new TreadmillProgramSetting(WorkoutEnum.ProgramType.TAPC_PROG);
        mProgramSetting.setSpeed(TreadmillSystemSettings.DEFAULT_SPEED);
        mProgramSetting.setIncline(TreadmillSystemSettings.DEFAULT_INCLINE);
//        mTapcApp.setProgramSetting(mProgramSetting);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                mProgramTime = progress + mProgramTimeMin;
                mProgramTimeText.setText(String.format(getString(R.string.program_time), mProgramTime));
                //                mTapcApp.getProgramSetting().setProgramTime(mProgramTime);
            }
        });
        mSeekBar.setMax(mProgramTimeMax - mProgramTimeMin);
        mSeekBar.setProgress(mStartTime - mProgramTimeMin);
    }

    @OnClick(R.id.program_start)
    protected void start(View v) {
        EventBus.getDefault().post(mProgramSetting);
        finish();
    }

    @OnClick(R.id.program_time_add)
    protected void addTime(View v) {
        mSeekBar.setProgress(mSeekBar.getProgress() + 1);
    }

    @OnClick(R.id.program_time_sub)
    protected void subTime(View v) {
        mSeekBar.setProgress(mSeekBar.getProgress() - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
