package com.tapc.platform.utils;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static com.tapc.platform.library.common.SystemSettings.mContext;

/**
 * Created by Administrator on 2017/9/29.
 */

public class CommonUtils {
    /**
     * 功能描述 : 是否显示输入法键盘
     *
     * @param : visibility  = false 隐藏显示
     */
    public static void setSoftInputVisibility(View view, boolean visibility) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        if (visibility) {
            imm.showSoftInput(view, 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
