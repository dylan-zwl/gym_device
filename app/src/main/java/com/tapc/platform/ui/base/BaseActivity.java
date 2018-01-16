package com.tapc.platform.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.tapc.platform.application.TapcApplication;
import com.tapc.platform.model.common.NoActionModel;
import com.trello.rxlifecycle2.components.RxActivity;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/5.
 */

public abstract class BaseActivity extends RxActivity {
    protected Context mContext;
    protected TapcApplication mTapcApp;

    protected abstract int getLayoutResID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int rid = getLayoutResID();
        if (rid != 0) {
            setContentView(rid);
            ButterKnife.bind(this);
        }
        mTapcApp = (TapcApplication) getApplication();
        mContext = this;
        initView();
    }

    protected void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        mTapcApp.addRefWatcher(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NoActionModel.getInstance().cleanNoActionCount();
        return super.dispatchTouchEvent(ev);
    }
}
