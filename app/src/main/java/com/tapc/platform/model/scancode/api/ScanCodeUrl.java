package com.tapc.platform.model.scancode.api;

/**
 * Created by Administrator on 2018/1/9.
 */

public class ScanCodeUrl {
    public static final String BASE_SERVER = "smartfit.shuhua.cn";
    public static final String SH_GYM = "http://smartfit.shuhua.cn/sports";
    public static final String GET_CODE = SH_GYM + "/index.php?m=admin&c=api&a=information";
    public static final String UPLOAD_DEVICE_INFORMATION = SH_GYM + "/index.php?m=admin&c=api&a=uploadInformation";

    public static final String GET_DEVICE_STATUS = SH_GYM + "/index.php?m=admin&c=api&a=uploadStatus";
    public static final String UPLOAD_SPORTS_DATA = SH_GYM + "/index.php?m=admin&c=api&a=uploadData";
    public static final String UPLOAD_DEVICE_ERROR = SH_GYM + "/index.php?m=admin&c=api&a=uploadFault";
    public static final String UPLOAD_DEVICE_DATA = SH_GYM + "/index.php?m=admin&c=api&a=uploadDeviceData";
    public static final String UNBUNDLIN_EQUIPMENT = SH_GYM + "/index.php?m=admin&c=api&a=unbundlingEquipment";

}
