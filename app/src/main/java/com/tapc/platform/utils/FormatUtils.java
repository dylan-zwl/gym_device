package com.tapc.platform.utils;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Administrator on 2017/3/21.
 */

public class FormatUtils {
    public static double formatDouble(double value) {
        BigDecimal bg = new BigDecimal(value).setScale(1, RoundingMode.HALF_UP);
        return bg.doubleValue();
    }

    public static double formatDouble(int bit, double value) {
        BigDecimal bg = new BigDecimal(value).setScale(bit, RoundingMode.HALF_UP);
        return bg.doubleValue();
    }

    public static float formatFloat(int bit, float value, RoundingMode roundingMode) {
        BigDecimal bg = new BigDecimal(value).setScale(bit, roundingMode);
        return bg.floatValue();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataTimeStr(String pattern, long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String getInputStream2String(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.ENGLISH, format, args);
    }
}
