package com.tapc.platform.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2017/9/29.
 */

public class CommonUtils {
    /**
     * 功能描述 : 是否显示输入法键盘
     *
     * @param : visibility  = false 隐藏显示
     */
    public static void setSoftInputVisibility(Context context, View view, boolean visibility) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (visibility) {
            imm.showSoftInput(view, 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
