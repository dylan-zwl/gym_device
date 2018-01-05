package com.tapc.platform.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseView;

public class WorkoutCtrl extends BaseView implements View.OnTouchListener {
    @Override
    protected int getContentView() {
        return R.layout.view_workout_ctrl;
    }

    public WorkoutCtrl(Context context) {
        this(context, null);
    }

    public WorkoutCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
