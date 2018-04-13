package com.tapc.platform.model.healthcat;

/**
 * Created by Administrator on 2018/4/11.
 */

public class Command {

    public static final byte D_LOGIN = 0x02;
    public static final byte S_LOGIN = 0x01;

    public static class Bike {
        //设备端发送命令
        public static final byte D_READ_INFO = 0x52;
        public static final byte D_READ_STATUS = 0x54;
        public static final byte D_SET_RESISTANCE = 0x56;
        public static final byte D_START_STOP = 0x5D;
        public static final byte D_LOCK = 0x5F;
        public static final byte D_UPLOAD_INFO = 0x5A;
        //服务器发送命令
        public static final byte S_READ_INFO = 0x51;
        public static final byte S_READ_STATUS = 0x53;
        public static final byte S_SET_RESISTANCE = 0x55;
        public static final byte S_START_STOP = 0x5C;
        public static final byte S_LOCK = 0x5E;
        public static final byte S_UPLOAD_INFO = 0x5B;
    }
}
