package com.tapc.platform.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseView;

import butterknife.BindView;

public class WorkoutOSD extends BaseView {
    @BindView(R.id.workout_osd_title)
    TextView mTitle;
    @BindView(R.id.workout_osd_value)
    TextView mValue;

    @Override
    protected int getContentView() {
        return R.layout.view_workout_osd;
    }

    public WorkoutOSD(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Workouts, 0, 0);
        String title = a.getString(R.styleable.Workouts_titleName);
        float titleSize = a.getDimension(R.styleable.Workouts_titleSize, 15);
        float unitSize = a.getDimension(R.styleable.Workouts_unitSize, 15);
        mTitle.setTextSize(titleSize);
        mValue.setTextSize(unitSize);
        setTitle(title);
        a.recycle();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setValue(String value) {
        mValue.setText(value);
    }
}
