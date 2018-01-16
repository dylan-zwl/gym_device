package com.tapc.platform.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.tapc.platform.model.common.NoActionModel;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/28.
 */

public abstract class BaseView extends LinearLayout {
    protected Context mContext;

    protected abstract int getLayoutResID();

    public BaseView(Context context) {
        super(context);
        init(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutResID(), this, true);
        ButterKnife.bind(this);
        mContext = context;
        initView();
    }

    protected void initView() {
    }

    public void onDestroy() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NoActionModel.getInstance().cleanNoActionCount();
        return super.dispatchTouchEvent(ev);
    }
}
