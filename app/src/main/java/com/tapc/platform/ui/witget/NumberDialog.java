package com.tapc.platform.ui.witget;

import android.content.Context;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseSystemView;

import butterknife.BindView;

public class NumberDialog extends BaseSystemView {
    @BindView(R.id.messages)
    TextView mMessage;

    private static final int NULL_NUM = -1;
    private static final int DELAY_TIME = 5000;
    private int mPressnumber = NULL_NUM;
    private Handler mKeyHandler = new Handler();

    public NumberDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.dialog_keyboard_msg;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return null;
    }

    public void setMsgShow(Object object) {
        String msg;
        if (object instanceof String) {
            msg = (String) object;
        } else {
            int id = (int) object;
            msg = mContext.getResources().getString(id);
        }
        if (msg != null) {
            mMessage.setText(msg);
            if (!isShown()) {
                mMessage.setVisibility(VISIBLE);
            }
        }
    }

    public void setVisivility(boolean flag) {
        if (flag) {
            show();
        } else {
            hide();
            mMessage.setText("");
        }
    }

    public int getPressNum(int num) {
        if (mPressnumber == NULL_NUM) {
            mPressnumber = num;
        } else {
            mPressnumber = (mPressnumber * 10 + num) % 100;
        }
        setMsgShow(mContext.getString(R.string.keyboard_input_str) + mPressnumber);
        if (!isShown()) {
            setVisivility(true);
        }
        mKeyHandler.removeCallbacks(mRunnable);
        mKeyHandler.postDelayed(mRunnable, DELAY_TIME);
        return mPressnumber;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mPressnumber = NULL_NUM;
            setVisivility(false);
        }
    };
}
