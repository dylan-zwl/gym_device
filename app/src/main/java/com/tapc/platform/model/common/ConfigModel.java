package com.tapc.platform.model.common;

import android.content.Context;

import com.tapc.platform.utils.PreferenceHelper;

import static com.tapc.platform.utils.PreferenceHelper.readBoolean;

/**
 * Created by Administrator on 2016/11/13.
 */

public class ConfigModel {
    //file
    public static final String SETTING_CONFIG = "setting_config";

    //key
    private static final String UNMANNED_RUN_CHECK = "unmanned_run_check";
    private static final String ERP = "erp";
    private static final String TEST = "test";
    private static final String LANGUAGE = "languageName";
    private static final String BACKLIGHT = "backlight";
    private static final String LOCAL_USER = "local_user";
    private static final String DEVICE_ID = "device_id";

    private static final String RFID = "rfid";
    private static final String SCAN_CODE = "scan_code";
    private static final String DEVICE_TYPE_ID = "device_type_id";
    private static final String LOGIN_RANDOMCODE = "login_randomcode";


    /**
     * @Description: 无人检测
     */
    public static boolean getUnmannedRunCheck(Context context, boolean defaults) {
        return readBoolean(context, SETTING_CONFIG, UNMANNED_RUN_CHECK, defaults);
    }

    public static void setUnmannedRunCheck(Context context, boolean data) {
        PreferenceHelper.write(context, SETTING_CONFIG, UNMANNED_RUN_CHECK, data);
    }

    /**
     * @Description: 待机
     */
    public static boolean getErpFunction(Context context, boolean defaults) {
        return PreferenceHelper.readBoolean(context, SETTING_CONFIG, ERP, defaults);
    }

    public static void setErpFunction(Context context, boolean data) {
        PreferenceHelper.write(context, SETTING_CONFIG, ERP, data);
    }

    /**
     * @Description: 测试
     */
    public static boolean getOpenTest(Context context, boolean defaults) {
        return readBoolean(context, SETTING_CONFIG, TEST, defaults);
    }

    public static void setOpenTest(Context context, boolean data) {
        PreferenceHelper.write(context, SETTING_CONFIG, TEST, data);
    }

    /**
     * @Description: 语言
     */
    public static String getLanguage(Context context, String defaults) {
        return PreferenceHelper.readString(context, SETTING_CONFIG, LANGUAGE, defaults);
    }

    public static void setLanguage(Context context, String data) {
        PreferenceHelper.write(context, SETTING_CONFIG, LANGUAGE, data);
    }

    public static String getLocalUser(Context context) {
        return PreferenceHelper.readString(context, SETTING_CONFIG, LOCAL_USER);
    }

    public static void setLocalUser(Context context, String data) {
        PreferenceHelper.write(context, SETTING_CONFIG, LOCAL_USER, data);
    }

    /**
     * @Description: 开机背光
     */
    public static int getBacklight(Context context, int defaults) {
        return PreferenceHelper.readInt(context, SETTING_CONFIG, BACKLIGHT, defaults);
    }

    public static void setBacklight(Context context, int data) {
        PreferenceHelper.write(context, SETTING_CONFIG, BACKLIGHT, data);
    }

    /**
     * @Description: 设备id
     */
    public static String getDeviceId(Context context, String defaults) {
        return PreferenceHelper.readString(context, SETTING_CONFIG, DEVICE_ID, defaults);
    }

    public static void setDeviceId(Context context, String data) {
        PreferenceHelper.write(context, SETTING_CONFIG, DEVICE_ID, data);
    }

    /**
     * 功能描述 : 扫码部分配置
     */

    /**
     * @Description: scan code
     */
    public static boolean getScanCode(Context context) {
        boolean defaults = true;
        return PreferenceHelper.readBoolean(context, SETTING_CONFIG, SCAN_CODE, defaults);
    }

    public static void setScanCode(Context context, boolean data) {
        PreferenceHelper.write(context, SETTING_CONFIG, SCAN_CODE, data);
    }

    /**
     * @Description: rfid
     */
    public static boolean getRfidFunction(Context context) {
        boolean defaults = false;
        return PreferenceHelper.readBoolean(context, SETTING_CONFIG, RFID, defaults);
    }

    public static void setRfidFunction(Context context, boolean data) {
        PreferenceHelper.write(context, SETTING_CONFIG, RFID, data);
    }

    /**
     * @Description: 设备机型id
     */
    public static int getDeviceTypeId(Context context, int defaults) {
        return PreferenceHelper.readInt(context, SETTING_CONFIG, DEVICE_TYPE_ID, defaults);
    }

    public static void setDeviceTypeId(Context context, String data) {
        PreferenceHelper.write(context, SETTING_CONFIG, DEVICE_TYPE_ID, data);
    }

    /**
     * @Description: 登录验证码
     */
    public static String getLoginRandomcode(Context context) {
        return PreferenceHelper.readString(context, SETTING_CONFIG, LOGIN_RANDOMCODE);
    }

    public static void setLoginRandomcode(Context context, String data) {
        PreferenceHelper.write(context, SETTING_CONFIG, LOGIN_RANDOMCODE, data);
    }

}
