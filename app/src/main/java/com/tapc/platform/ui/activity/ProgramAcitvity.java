package com.tapc.platform.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.IntentUtils;

import java.util.ArrayList;

import butterknife.BindView;

public class ProgramAcitvity extends BaseActivity {
    @BindView(R.id.program_viewpage)
    ViewPager mProgramViewpage;

    private ArrayList<View> mListViews;
    private int[] mListItemRid = {R.id.program1, R.id.program2, R.id.program3, R.id.program4, R.id.program5, R.id
            .program6, R.id.program7, R.id.program8, R.id.program9, R.id.program10, R.id.program11, R.id.program12};

    @Override
    protected int getContentView() {
        return R.layout.activity_program;
    }

    @Override
    protected void initView() {
        super.initView();
        mListViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        mListViews.add(mInflater.inflate(R.layout.activity_program1, null));
        mListViews.add(mInflater.inflate(R.layout.activity_program2, null));

        for (int i = 0; i < mListItemRid.length; i++) {
            mListViews.get(i / 6).findViewById(mListItemRid[i]).setOnClickListener(mProgramListener);
        }
        mListViews.get(0).findViewById(R.id.program_nextbutton).setOnClickListener(mSetCurrentItemListener);
        mListViews.get(1).findViewById(R.id.program_prebutton).setOnClickListener(mSetCurrentItemListener);

        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mListViews.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mListViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mListViews.get(position));
                return mListViews.get(position);
            }
        };

        mProgramViewpage.setAdapter(mPagerAdapter);
        mProgramViewpage.setCurrentItem(0);
        mProgramViewpage.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
    }

//    @OnClick(R.id.program_nextbutton)
//    void nextbuttonOnClick(View v) {
//        mProgramViewpage.setCurrentItem(1);
//    }
//
//    @OnClick(R.id.program_prebutton)
//    void prebuttonOnClick(View v) {
//        mProgramViewpage.setCurrentItem(0);
//    }
//
//    @OnClick({R.id.program1, R.id.program2, R.id.program3, R.id.program4, R.id.program5, R.id.program6, R.id
//            .program7, R.id.program8, R.id.program9, R.id.program10, R.id.program11, R.id.program12})
//    void program(View v) {
//        for (int i = 0; i < mListItemRid.length; i++) {
//            if (v.getId() == mListItemRid[i]) {
//                start(i + 1);
//            }
//        }
//    }

    private View.OnClickListener mSetCurrentItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.program_nextbutton:
                    mProgramViewpage.setCurrentItem(1);
                    break;
                case R.id.program_prebutton:
                    mProgramViewpage.setCurrentItem(0);
                    break;
            }
        }
    };

    private View.OnClickListener mProgramListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mListItemRid.length; i++) {
                if (v.getId() == mListItemRid[i]) {
                    start(i + 1);
                }
            }
        }
    };

    private void start(int program) {
        Bundle bundle = new Bundle();
        bundle.putInt("program", program);
        IntentUtils.startActivity(this, SetProgramTimeAcitvity.class, bundle, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
