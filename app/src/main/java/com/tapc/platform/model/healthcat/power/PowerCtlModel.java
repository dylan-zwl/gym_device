package com.tapc.platform.model.healthcat.power;

import android.content.Context;

import com.tapc.platform.model.healthcat.AckStatus;
import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.Command;

/**
 * Created by Administrator on 2018/4/12.
 */

public class PowerCtlModel extends BaseCtlModel {
    private PowerManage mManage;
    private PowerCtlLister mCtlLister;

    public PowerCtlModel(Context context) {
        super(context);
    }

    @Override
    public void sendHeartbeat() {
        mManage.sendHeartbeat(Command.Power.D_UPLOAD_INFO, (PowerData) mCtlLister.serverReadInfo());
    }

    @Override
    public BaseCommunicationManage getManage() {
        mManage = new PowerManage();
        return mManage;
    }

    public interface PowerCtlLister extends BaseListener {

    }

    public void setCtlLister(PowerCtlLister listener) {
        setBaseListener(listener);
        this.mCtlLister = listener;
    }

    public boolean login() {
        return mManage.login();
    }

    @Override
    protected void recvMessage(byte[] dataBuffer) {
        byte cmd = mManage.getDataPack().getComman(dataBuffer);
        byte[] data = null;
        if (cmd != 0) {
            data = mManage.getDataPack().getCommanDataPack(dataBuffer);
        }
        switch (cmd) {
            case Command.S_LOGIN:
                byte loginStatus = mManage.getDataPack().getCommanData(data, 0);
                if (loginStatus != -1) {
                    switch (loginStatus) {
                        case AckStatus.SUCCESS:
                            mCtlLister.login(true);
                            startHeartbeat();
                            break;
                        case AckStatus.FAIL:
                            mCtlLister.login(false);
                            break;
                    }
                }
                break;
            case Command.Power.S_SET_HEART_TIME:
                int time = mManage.getDataPack().getCommanData(data, 0);
                setHeatbeatTime(time);
                startHeartbeat();
                mManage.sendHeartbeat(Command.Power.D_READ_INFO, (PowerData) mCtlLister.serverReadInfo());
                mManage.ackStatus(Command.Power.D_SET_HEART_TIME, AckStatus.SUCCESS);
                break;
            case Command.Power.S_READ_INFO:
                mManage.sendHeartbeat(Command.Power.D_READ_INFO, (PowerData) mCtlLister.serverReadInfo());
                break;
            case Command.Power.S_START_STOP:
                setRunStatus(Command.Power.D_START_STOP, data);
                break;
            case Command.Power.S_LOCK:
                setLock(Command.Power.D_LOCK, data);
                break;
            case Command.Power.S_UPLOAD_INFO:
                mHeartBeat.resetCount();
                break;
        }
    }
}
