package com.tapc.platform.model.healthcat.bike;

import android.content.Context;

import com.tapc.platform.model.healthcat.AckStatus;
import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.Command;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BikeCtlModel extends BaseCtlModel {
    private BikeManage mManage;
    private BikeCtlLister mCtlLister;
    private BikeCtlLister mBikeCtlLister;

    public BikeCtlModel(Context context) {
        super(context);
    }

    @Override
    public void sendHeartbeat() {
        mManage.sendHeartbeat();
    }

    @Override
    public BaseCommunicationManage getManage() {
        mManage = new BikeManage();
        return mManage;
    }

    public interface BikeCtlLister extends BaseCtlModel.BaseListener {
        void serverReadInfo();

        void serverReadStatus();

        void serverSetResistance(byte resistance);

        void serverSetRunStatus(boolean isStart);

        void serverSetLock(boolean lock);
    }

    public void setBikeCtlLister(BikeCtlLister listener) {
        setBaseListener(listener);
        this.mBikeCtlLister = listener;
    }

    public boolean login() {
        return mManage.login();
    }

    @Override
    protected void recvMessage(byte[] dataBuffer) {
        byte cmd = mManage.getDataPack().getComman(dataBuffer);
        byte[] data = null;
        if (cmd != 0) {
            data = mManage.getDataPack().getData(dataBuffer);
            if (data == null) {
                return;
            }
        }
        switch (cmd) {
            case Command.S_LOGIN:
                byte loginStatus = mManage.getDataPack().getDataByte(data, 0);
                if (loginStatus != -1) {
                    switch (loginStatus) {
                        case AckStatus.SUCCESS:
                            mBikeCtlLister.login(true);
                            break;
                        case AckStatus.FAIL:
                            mBikeCtlLister.login(false);
                            break;
                    }
                }
                break;
            case Command.Bike.S_READ_INFO:
                mBikeCtlLister.serverReadInfo();
                break;
            case Command.Bike.S_READ_STATUS:
                mBikeCtlLister.serverReadStatus();
                break;
            case Command.Bike.S_SET_RESISTANCE:
                byte resistance = mManage.getDataPack().getDataByte(data, 0);
                if (resistance != -1) {
                    mBikeCtlLister.serverSetResistance(resistance);
                }
                break;
            case Command.Bike.S_START_STOP:
                byte runStatus = mManage.getDataPack().getDataByte(data, 0);
                if (runStatus != -1) {
                    switch (runStatus) {
                        case AckStatus.START:
                            mBikeCtlLister.serverSetRunStatus(true);
                            break;
                        case AckStatus.STOP:
                            mBikeCtlLister.serverSetRunStatus(false);
                            break;
                    }
                }
                break;
            case Command.Bike.S_LOCK:
                byte lock = mManage.getDataPack().getDataByte(data, 0);
                if (lock != -1) {
                    switch (lock) {
                        case AckStatus.LOCK:
                            mBikeCtlLister.serverSetLock(true);
                            break;
                        case AckStatus.UNLOCK:
                            mBikeCtlLister.serverSetLock(false);
                            break;
                    }
                }
                break;
            case Command.Bike.S_UPLOAD_INFO:
                mHeartBeat.resetCount();
                break;
        }
    }

}
