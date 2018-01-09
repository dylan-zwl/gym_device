package com.tapc.platform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tapc.platform.R;
import com.tapc.platform.library.common.AppSettings;
import com.tapc.platform.library.common.CommonEnum;
import com.tapc.platform.library.common.TreadmillSystemSettings;
import com.tapc.platform.library.util.WorkoutEnum;
import com.tapc.platform.library.workouting.WorkOuting;
import com.tapc.platform.model.vaplayer.PlayEntity;
import com.tapc.platform.model.vaplayer.VaPlayer;
import com.tapc.platform.model.vaplayer.VaRecordPosition;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

import java.math.RoundingMode;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ScenePlayActivity extends BaseActivity implements View.OnTouchListener, Observer {
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;
    @BindView(R.id.videoMessage)
    TextView mVideoMessage;
    @BindView(R.id.play_layout)
    RelativeLayout mPlayLayout;
    @BindView(R.id.show_or_hide_text_iv)
    ImageView show_or_hide_text_iv;
    @BindView(R.id.full_switch)
    ImageView full_switch;
    @BindView(R.id.videoname)
    TextView mVideoName;
    @BindView(R.id.videodepiction)
    TextView mVideoDepiction;
    @BindView(R.id.addBtn)
    ImageButton mVaSpeedInc;
    @BindView(R.id.minusBtn)
    ImageButton mVaSpeedDec;

    private static final int ADD_TOUCH = 0;
    private static final int SUB_TOUCH = 1;
    private VaPlayer mPlayer;
    private PlayEntity mPlayEntity;
    private static VaRecordPosition sVaRecordPosition;

    private boolean isFullScreen = false;
    private boolean isLongTouch = true;
    private int mDelayTime = 150;

    private RelativeLayout.LayoutParams mPlayParams;
    private WorkOuting mWorkOuting = WorkOuting.getInstance();

    public static void launch(Context c, PlayEntity entity) {
        Intent i = new Intent(c, ScenePlayActivity.class);
        i.putExtra("play_video", entity);
        c.startActivity(i);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scene_play;
    }

    @Override
    protected void initView() {
        super.initView();
        mVaSpeedInc.setOnTouchListener(this);
        mVaSpeedDec.setOnTouchListener(this);
        mWorkOuting.subscribeObserverNotification(this);
        initPlayEntity();
        startPlay();
        if (!mWorkOuting.isRunning()) {
            mWorkOuting.start();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        WorkoutEnum.WorkoutUpdate workoutUpdate = (WorkoutEnum.WorkoutUpdate) arg;
        if (workoutUpdate != null) {
            switch (workoutUpdate) {
                case UI_STOP:
                    finish();
                    break;
                case UI_PAUSE:
                    setPlayPause(true);
                    break;
                case UI_RESUME:
                    setPlayPause(false);
                    break;
                case UI_LEFT:
                    if (AppSettings.getPlatform() == CommonEnum.Platform.TCC8935) {
                        mPlayer.initVideoSpeed(100000, 200000);
                    } else {
                        mPlayer.initVideoSpeed(100, 200);
                    }
                    break;
            }
        }
    }

    private void initPlayEntity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPlayEntity = (PlayEntity) bundle.get("play_video");
        }
    }

    private void startPlay() {
        if (mPlayEntity == null) {
            return;
        }
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer = new VaPlayer(mSurfaceView.getHolder());
                mPlayer.init();
                mPlayer.setBackMusicVisibility(false);
                mPlayer.setPlayPosition(sVaRecordPosition);
                mPlayer.setPlayerListener(new VaPlayer.PlayerListener() {
                    @Override
                    public void setIncline(int videoIncline) {
                        mWorkOuting.onLeftPanel(getDeviceIncline(videoIncline));
                    }

                    @Override
                    public void setPlaySpeed(MediaPlayer player, int videoSpeed) {

                    }

                    @Override
                    public void error(String text) {

                    }

                    @Override
                    public void videoMessage(String text) {
                        //  mVideoMessage.setText(text);
                    }
                });
                mPlayer.start(mPlayEntity);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void setPlayPause(boolean isPause) {
        if (mPlayer != null) {
            mPlayer.setPause(isPause);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mVaPlayerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_TOUCH:
                    if (isLongTouch) {
                        addClick(null);
                        mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(ADD_TOUCH), mDelayTime);
                    }
                    break;
                case SUB_TOUCH:
                    if (isLongTouch) {
                        minutClick(null);
                        mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(SUB_TOUCH), mDelayTime);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private int getDeviceIncline(int videoIncline) {
        float incline = 0;
        if (mWorkOuting.isRunning()) {
            incline = videoIncline;
            incline /= 1000;
            incline *= (TreadmillSystemSettings.MAX_INCLINE - TreadmillSystemSettings.MIN_INCLINE);
            incline /= TreadmillSystemSettings.STEP_INCLINE;
            incline = FormatUtils.formatFloat(1, incline, RoundingMode.HALF_UP);
            int inclinetemp = (int) incline;
            incline = inclinetemp * TreadmillSystemSettings.STEP_INCLINE;
            Logger.d("va incline set " + incline);
        }
        return (int) incline;
    }

    @OnClick(R.id.addBtn)
    void addClick(View v) {
        mWorkOuting.onRightKeypadAdd();
    }

    @OnClick(R.id.minusBtn)
    void minutClick(View v) {
        mWorkOuting.onRightKeypadSub();
        if (mPlayer != null) {
            mPlayer.setPlaySpeed(mWorkOuting.getWorkout().getSpeed());
        }
    }

    @OnLongClick(R.id.addBtn)
    boolean subLongClick(View v) {
        isLongTouch = true;
        mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(ADD_TOUCH), mDelayTime);
        return true;
    }

    @OnLongClick(R.id.minusBtn)
    boolean addLongClick(View v) {
        isLongTouch = true;
        mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(SUB_TOUCH), mDelayTime);
        return true;
    }

    @OnClick(R.id.change_play_list)
    void changePlayListClick(View v) {
        SceneRunActivity.launch(this);
        finish();
    }

    /**
     * 点击全屏
     *
     * @param v
     */
    @OnClick(R.id.full_switch)
    void fullSwitch(View v) {
        if (isFullScreen) {
            isFullScreen = false;
            mPlayLayout.setPadding(0, 0, 0, 0);
            mPlayLayout.setLayoutParams(mPlayParams);
            full_switch.setImageResource(R.drawable.btn_full_screen);
        } else {
            isFullScreen = true;
            mPlayLayout.setPadding(0, 0, 0, 0);
            mPlayParams = (RelativeLayout.LayoutParams) mPlayLayout.getLayoutParams();
            mPlayLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            full_switch.setImageResource(R.drawable.btn_unfullscreen);
        }
    }

    @OnClick(R.id.show_or_hide_text_iv)
    void setVideoMassage(View v) {
        if (mVideoMessage.isShown()) {
            mVideoMessage.setVisibility(View.GONE);
            show_or_hide_text_iv.setBackgroundResource(R.drawable.btn_scene_play_msg_hide);
        } else {
            mVideoMessage.setVisibility(View.VISIBLE);
            show_or_hide_text_iv.setBackgroundResource(R.drawable.btn_scene_play_msg_show);
        }
    }

    public static VaRecordPosition getVaRecordPosition() {
        return sVaRecordPosition;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isLongTouch = false;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            sVaRecordPosition = mPlayer.getPlayPosition();
            mPlayer.stop();
            mPlayer = null;
        }
    }
}
