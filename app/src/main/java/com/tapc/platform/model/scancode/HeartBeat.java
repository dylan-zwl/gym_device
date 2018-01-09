package com.tapc.platform.model.scancode;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/1/9.
 */

public class HeartBeat {
    /**
     * 服务器连接的心跳包
     */
    private Timer mHeartbeatTimer;
    private int mNotHeartbeatCount;
    private int mNotHeartbeatNumber;
    private Listener mListener;

    public void start() {
        if (mListener == null) {
            return;
        }
        cancelHearbeatTimer();
        mListener.sendHeartbeat();
        mHeartbeatTimer = new Timer();
        mHeartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mNotHeartbeatCount++;
                if (mNotHeartbeatCount >= 2) {
                    cancelHearbeatTimer();
                    mNotHeartbeatNumber++;
                    Log.d("recv hearbeat", "failed number : " + mNotHeartbeatNumber);
                    mListener.connectFailed();
                    return;
                }
                mListener.sendHeartbeat();
            }
        }, 10000, 10000);
    }

    public void stop() {
        cancelHearbeatTimer();
    }

    public void clearCount() {
        mNotHeartbeatCount = 0;
    }

    public interface Listener {
        void connectFailed();

        void sendHeartbeat();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void cancelHearbeatTimer() {
        mNotHeartbeatCount = 0;
        if (mHeartbeatTimer != null) {
            mHeartbeatTimer.cancel();
            mHeartbeatTimer = null;
        }
    }
}
