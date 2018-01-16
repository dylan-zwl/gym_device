package com.tapc.platform.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.ui.base.BaseView;
import com.tapc.platform.utils.FormatUtils;
import com.tapc.platform.utils.RxjavaUtils;
import com.tapc.platform.utils.TypedArrayUtils;

import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DeviceCtl extends BaseView implements View.OnTouchListener {
    @BindView(R.id.show_title)
    TextView mTitle;
    @BindView(R.id.show_value_text)
    TextView mValue;
    @BindView(R.id.show_unit)
    TextView mUnit;

    @BindView(R.id.Add)
    Button mCtlAdd;
    @BindView(R.id.Sub)
    Button mCtlSub;

    private float mNowValue;
    private float mSetValuef;
    private Listener mListener;
    private Disposable mDisposable;
    private float mStepValue;
    private float mMaxValue;
    private float mMinValue;
    private int mDelayTime = 100;
    private float mDefValue;

    private enum ValueChange {
        ADD, SUB,
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.view_workout_ctrl;
    }

    public DeviceCtl(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DeviceCtl);
        TypedArrayUtils.setTextView(mTitle, array, R.styleable.DeviceCtl_ctlTitle);
        TypedArrayUtils.setTextView(mUnit, array, R.styleable.DeviceCtl_ctlUnit);
        array.recycle();
        mCtlAdd.setOnTouchListener(this);
        mCtlSub.setOnTouchListener(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    public double getValue() {
        return mNowValue;
    }

    public void setValue(float value) {
        mSetValuef = FormatUtils.formatFloat(1, value, RoundingMode.HALF_UP);
        this.mNowValue = mSetValuef;
        mValue.setText(formatShowStr(mSetValuef));
    }

    public String formatShowStr(float value) {
        return String.format("%.1f", value);
    }

    @OnClick(R.id.Add)
    void onAddClick(View v) {
        if (mListener != null) {
            mListener.onAddClick();
        }
    }

    @OnClick(R.id.Sub)
    void onSubClick(View v) {
        if (mListener != null) {
            mListener.onSubClick();
        }
    }

    @OnLongClick(R.id.Add)
    boolean onAddLongClick(View v) {
        startObserable(ValueChange.ADD);
        return true;
    }

    @OnLongClick(R.id.Sub)
    boolean onSubLongClick(View v) {
        startObserable(ValueChange.SUB);
        return true;
    }

    @OnClick(R.id.show_value_text)
    protected void valueClick(View v) {
        if (mListener != null) {
            mListener.onValueTextClick(mNowValue);
        }
    }

    public void cancelObserable() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
            if (mListener != null) {
                mListener.setDeviceValue(mSetValuef);
            }
        }
    }

    void startObserable(final ValueChange valueChange) {
        cancelObserable();
        mDisposable = RxjavaUtils.interval(mDelayTime, mDelayTime, TimeUnit.MILLISECONDS, new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                switch (valueChange) {
                    case ADD:
                        mSetValuef = mSetValuef + mStepValue;
                        if (mSetValuef > mMaxValue) {
                            mSetValuef = mMaxValue;
                        }
                        mValue.setText(formatShowStr(mSetValuef));
                        break;
                    case SUB:
                        mSetValuef = mSetValuef - mStepValue;
                        if (mSetValuef < mMinValue) {
                            mSetValuef = mMinValue;
                        }
                        mValue.setText(formatShowStr(mSetValuef));
                        break;
                }
            }
        }, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                cancelObserable();
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return false;
    }

    public void setOnClickListener(@NonNull Listener listener) {
        this.mListener = listener;
    }

    public interface Listener {
        void onAddClick();

        void onSubClick();

        void onValueTextClick(float value);

        void setDeviceValue(float value);
    }

    public void setConfig(float minValue, float maxValue, float stepValue, float defValue, int delayTimeMs) {
        this.mMinValue = minValue;
        this.mMaxValue = maxValue;
        this.mStepValue = stepValue;
        this.mDefValue = defValue;
        this.mDelayTime = delayTimeMs;
        setValue(defValue);
    }

    public void setPause() {
        mCtlAdd.setBackgroundResource(R.drawable.btn_add_p);
        mCtlSub.setBackgroundResource(R.drawable.btn_sub_p);
        mCtlAdd.setEnabled(false);
        mCtlSub.setEnabled(false);
        mTitle.setEnabled(false);
    }

    public void setResume() {
        mCtlAdd.setBackgroundResource(R.drawable.btn_add);
        mCtlSub.setBackgroundResource(R.drawable.btn_sub);
        mCtlAdd.setEnabled(true);
        mCtlSub.setEnabled(true);
        mTitle.setEnabled(true);
    }

    public float getMinValue() {
        return mMinValue;
    }

    public float getMaxValue() {
        return mMaxValue;
    }
}
