package com.tapc.platform.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/9/29.
 */

public class CommonUtils {
    @SuppressLint("SimpleDateFormat")
    public static String getDataTimeStr(String pattern, long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}
