package com.tapc.platform.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseActivity;

import butterknife.BindView;

public class SportResultActivity extends BaseActivity {
    @BindView(R.id.result_distance)
    TextView mTextDistance;
    @BindView(R.id.result_time)
    TextView mTextTime;
    @BindView(R.id.result_speed)
    TextView mTextSpeed;
    @BindView(R.id.result_calorie)
    TextView mTextCalorie;

    public static void launch(Context c, String type) {
        Intent i = new Intent(c, SportResultActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("type", type);
        c.startActivity(i);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sportresult;
    }
}
