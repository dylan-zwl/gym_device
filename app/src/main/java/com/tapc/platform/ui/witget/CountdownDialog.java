package com.tapc.platform.ui.witget;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseSystemView;
import com.tapc.platform.utils.SoundCtlUtils;
import com.tapc.platform.utils.WindowManagerUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/29.
 */

public class CountdownDialog extends BaseSystemView {
    @BindView(R.id.countdown_text)
    TextView mCountdown;

    private Handler mHandler;
    private FinishedListener mFinishedListener;
    private int mNumbers;
    private Timer mTimer;

    public CountdownDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.dialog_countdown;
    }

    @Override
    protected void initView() {
        super.initView();
        mHandler = new Handler();
    }

    @Override
    public void show() {
        super.show();
        startTimer();
        countdownVoice();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        cancelTimer();
        mCountdown.setText("");
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimer() {
        mNumbers = 3;
        cancelTimer();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mNumbers <= 0) {
                            mTimer.cancel();
                            if (mFinishedListener != null) {
                                mFinishedListener.onFinished();
                            }
                            hide();
                        } else {
                            mCountdown.setText("" + mNumbers);
                        }
                        mNumbers--;
                    }
                });
            }
        }, 0, 1000);
    }

    private void countdownVoice() {
        SystemClock.sleep(200);
        SoundCtlUtils.getInstance().playBeep(mContext, R.raw.start);
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT, Gravity.TOP);
    }

    public void setFinishedListener(FinishedListener finishedListener) {
        this.mFinishedListener = finishedListener;
    }

    public interface FinishedListener {
        void onFinished();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
