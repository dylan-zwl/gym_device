package com.tapc.platform.ui.witget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.application.TapcApplication;
import com.tapc.platform.broadcast.send.WorkoutBroadcase;
import com.tapc.platform.entity.DeviceWorkout;
import com.tapc.platform.library.controller.MachineController;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.ui.base.BaseSystemView;
import com.tapc.platform.utils.IntentUtils;
import com.tapc.platform.utils.WindowManagerUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tapc.platform.library.controller.MachineStatusController.DEVICE_ERROR_STATUS;
import static com.tapc.platform.library.controller.MachineStatusController.DEVICE_SAFE_KEY_STATUS;

/**
 * Created by Administrator on 2017/9/7.
 */

public class ErrorDialog extends BaseSystemView {
    @BindView(R.id.error_safekey)
    LinearLayout mSafeKeyLL;
    @BindView(R.id.error_code)
    LinearLayout mErrorCodeLL;

    @BindView(R.id.error_code_tx)
    TextView mErrorCodeTx;

    private boolean isHideError;
    private int mOldErrorStaus;

    private boolean isShowSafeKey = false;
    private boolean isShowError = false;

    public ErrorDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        super.initView();
        IntentUtils.registerReceiver(mContext, mErrorReceiver, DEVICE_ERROR_STATUS);
        IntentUtils.registerReceiver(mContext, mSafeKeyReceiver, DEVICE_SAFE_KEY_STATUS);
        int safekey = MachineController.getInstance().getSafeKeyStatus();
        setSafeKeyShow(safekey);
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT, Gravity.TOP);
    }

    @Override
    protected int getContentView() {
        return R.layout.dialog_machine_status;
    }

    @OnClick(R.id.noShowError_l)
    void leftOnClick() {
        isHideError = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isHideError = false;
            }
        }, 2000);
    }

    @OnClick(R.id.noShowError_r)
    void rightOnClick() {
        if (isHideError) {
            mErrorCodeLL.setVisibility(GONE);
            isShowError = false;
            if (mErrorReceiver != null) {
                IntentUtils.unregisterReceiver(mContext, mErrorReceiver);
            }
            resetDialogStatus();
        }
    }

    private BroadcastReceiver mErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DEVICE_ERROR_STATUS, 0);
            status = status & 0xffff;
            Log.e("error code", "" + status);
            if (mOldErrorStaus == status) {
                return;
            }
            if (status == 0) {
                mErrorCodeTx.setText("");
                mErrorCodeLL.setVisibility(GONE);
                isShowError = false;
            } else {
                String errorStr = Integer.toHexString(status);
                //                String text = String.format("2s%", errorStr);
                mErrorCodeTx.setText(errorStr);
                mErrorCodeLL.setVisibility(VISIBLE);
                isShowError = true;
            }
            resetDialogStatus();
            mOldErrorStaus = status;
        }
    };

    private BroadcastReceiver mSafeKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DEVICE_SAFE_KEY_STATUS, 0);
            Log.e(this.getClass().getName(), "safekey " + status);
            setSafeKeyShow(status);
        }
    };

    private void setSafeKeyShow(int status) {
        if (status == 0) {
            mSafeKeyLL.setVisibility(GONE);
            isShowSafeKey = false;
        } else {
            mSafeKeyLL.setVisibility(VISIBLE);
            isShowSafeKey = true;
        }
        resetDialogStatus();
    }

    private void resetDialogStatus() {
        if (isShowSafeKey || isShowError) {
            show();
            if (WorkOuting.getInstance().isRunning()) {
                TapcApplication.getInstance().getService().getCountdownDialog().dismiss();
                WorkoutBroadcase.send(mContext, DeviceWorkout.STOP);
            }
        } else {
            dismiss();
        }
    }
}
