package com.chx.livemaker.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cangHX
 * on 2018/12/25  18:59
 */
public class LiveSpManager {

    private static final String SP_NAME = "live";
    private static final int MODE = Context.MODE_PRIVATE;

    private static final String KEY_LEVEL = "level";

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE);
    }

    //设置当前设备camera需要进行降级，以确保下次不会再使用高等级camera进行尝试
    public static void setCameraLevelDown(Context context) {
        SharedPreferences preferences = getSp(context);
        preferences.edit().putBoolean(KEY_LEVEL, true).apply();
    }

    //当前设备camera是否需要降级
    public static boolean isCameraLevel(Context context) {
        return getSp(context).getBoolean(KEY_LEVEL, false);
    }

}
