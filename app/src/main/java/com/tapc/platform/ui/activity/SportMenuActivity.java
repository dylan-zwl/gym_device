package com.tapc.platform.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.model.scancode.dao.response.User;
import com.tapc.platform.ui.adpater.SportMenuAdapter;
import com.tapc.platform.ui.base.BaseActivity;

import butterknife.BindView;

public class SportMenuActivity extends BaseActivity {
    @BindView(R.id.sport_menu_list)
    ListView mListView;

    @BindView(R.id.sport_menu_user_name)
    TextView mUserName;

    private User mUser;
    //    private List<SportData> mSportMenuList;
    private SportMenuAdapter mSportMenuAdapter;
    private int mProgramTime = 0;

    public static void launch(Context c) {
        Intent i = new Intent(c, SportMenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(i);
    }

    @Override
    protected int getLayoutResID() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_leke_sport_plan);
        initView();
    }

    @Override
    protected void initView() {
//        mUser = TapcApp.getInstance().mainActivity.getUser();
//        if (mUser != null) {
//            mUserName.setText(mUser.getName());
//            mSportMenuList = mUser.getPlan_load();
//            if (mSportMenuList == null) {
//                mSportMenuList = new ArrayList<SportData>();
//            }
//            mSportMenuAdapter = new SportMenuAdapter(this, mSportMenuList);
//            mListView.setAdapter(mSportMenuAdapter);
//        }
    }

//    @OnClick(R.id.start)
//    protected void startOnclick() {
//        start();
//    }
//
//    @OnClick(R.id.exit)
//    protected void exitOnclick() {
//        finish();
//    }
//
//    private void initAllEntity() {
//        if (mSportMenuList.size() > 0) {
//            mProgramTime = 0;
//            List<IntervalInfo> intervalList = initIntervalInfoList(mSportMenuList);
//            if (intervalList != null && !intervalList.isEmpty()) {
//                FitnessSetAllEntity allEntity = new FitnessSetAllEntity();
//                TapcApp.getInstance().getSportsEngin().openFitness(allEntity);
//                allEntity.setProgramEntity(ProgramType.TAPC_PROG, 1, SystemSettingsHelper.WEIGHT,
//                        SystemSettingsHelper.DEFAULT_INCLINE, SystemSettingsHelper.DEFAULT_SPEED);
//                if (mProgramTime % 60 > 0) {
//                    mProgramTime = mProgramTime / 60 + 1;
//                } else {
//                    mProgramTime = mProgramTime / 60;
//                }
//                allEntity.setProgramTime(mProgramTime);
//                allEntity.setIntervalList(intervalList);
//            }
//        } else {
//            TapcApp.getInstance().getSportsEngin().openFitness(null);
//        }
//    }
//
//    private void start() {
//        initAllEntity();
//        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private List<IntervalInfo> initIntervalInfoList(List<SportData> sportMenuList) {
//        if (sportMenuList != null) {
//            List<IntervalInfo> intervalList = new ArrayList<IntervalInfo>();
//            for (int i = 0; i < sportMenuList.size(); i++) {
//                SportData sportMenuData = new SportData();
//                sportMenuData = sportMenuList.get(i);
//                int timeSec = Integer.valueOf(sportMenuData.getTime());
//                mProgramTime = mProgramTime + timeSec;
//                double speed = Double.valueOf(sportMenuData.getSpeed());
//                double incline = Double.valueOf(sportMenuData.getIncline());
//                IntervalInfo info = new IntervalInfo();
//                info.setChangeTime(timeSec);
//                info.setSpeed(speed);
//                info.setIncline(incline);
//                info.setWorkoutStage(WorkoutStage.NORMAL);
//                intervalList.add(info);
//            }
//            return intervalList;
//        } else {
//            return null;
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
