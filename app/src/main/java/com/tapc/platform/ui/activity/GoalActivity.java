package com.tapc.platform.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/5.
 */

public class GoalActivity extends BaseActivity {
    @BindView(R.id.goal_screen1)
    RelativeLayout mLinearLayoutScreen1;
    @BindView(R.id.goal_title)
    TextView mGoalTitle;
    @BindView(R.id.goal_value)
    TextView mTextView;
    @BindView(R.id.goal_seekbar)
    SeekBar mSeekBar;

    public enum GoalModeType {
        TIME, DISTANCE, CALORIE
    }

    public static void launch(Context c, GoalModeType modeType) {
        Intent i = new Intent(c, GoalActivity.class);
        i.putExtra("key_goal_mode", modeType);
        c.startActivity(i);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_goal;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @OnClick(R.id.goal_sub)
    void sub(View v) {
        mSeekBar.setProgress(mSeekBar.getProgress() - 1);
    }

    @OnClick(R.id.goal_add)
    void add(View v) {
        mSeekBar.setProgress(mSeekBar.getProgress() + 1);
    }
}
