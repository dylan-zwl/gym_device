package com.tapc.platform.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseView;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/11/12.
 */

public class WorkoutGoal extends BaseView {
    @BindView(R.id.goalProgress)
    ProgressBar mGoalProgress;
    @BindView(R.id.totalTitle)
    TextView mTitle;
    @BindView(R.id.totalUnit)
    TextView mUnit;

    public WorkoutGoal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.view_workout_goal;
    }

    public void SetRange(int min, int max) {
        mGoalProgress.setMax(max);
    }

    public float getPos() {
        return mGoalProgress.getProgress();
    }

    public void setPos(float pos) {
        mGoalProgress.setProgress((int) pos);
        invalidate();
    }

    public void setTitleName(String title, String unit) {
        mTitle.setText(title);
        mUnit.setText(unit);
    }
}
