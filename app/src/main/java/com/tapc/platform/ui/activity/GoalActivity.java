package com.tapc.platform.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseActivity;

/**
 * Created by Administrator on 2018/1/5.
 */

public class GoalActivity extends BaseActivity {

    public enum GoalModeType {
        TIME, DISTANCE, CALORIE
    }

    public static void launch(Context c, GoalModeType modeType) {
        Intent i = new Intent(c, GoalActivity.class);
        i.putExtra("key_goal_mode", modeType);
        c.startActivity(i);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_goal;
    }
}
