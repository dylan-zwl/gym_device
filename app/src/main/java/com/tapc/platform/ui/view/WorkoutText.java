package com.tapc.platform.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseView;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/11/12.
 */

public class WorkoutText extends BaseView {
    @BindView(R.id.workout_text_title)
    TextView mTitle;
    @BindView(R.id.workout_text_unit)
    TextView mUnit;
    @BindView(R.id.workout_text_value)
    TextView mValue;

    @Override
    protected int getLayoutResID() {
        return R.layout.view_workout_text;
    }

    public WorkoutText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Workouts, 0, 0);
        String title = a.getString(R.styleable.Workouts_titleName);
        String unit = a.getString(R.styleable.Workouts_unitName);
        mTitle.setText(title);
        mUnit.setText(unit);
    }

    public void setValue(String value) {
        mValue.setText(value);
    }
}
