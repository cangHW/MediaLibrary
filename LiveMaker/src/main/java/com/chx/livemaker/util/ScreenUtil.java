package com.chx.livemaker.util;

import android.content.res.Resources;

/**
 * Created by cangHX
 * on 2019/01/14  17:44
 */
public class ScreenUtil {

    /**
     * 屏幕宽度
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 屏幕高度
     */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
