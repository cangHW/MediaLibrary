package com.chx.livemaker.util;

import android.util.Log;

/**
 * Created by cangHX
 * on 2018/12/17  10:57
 */
public class LiveLogger {

    private static boolean DEBUG = true;

    private String TAG;

    public static void setIsDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

    private LiveLogger(String tag) {
        this.TAG = tag;
    }

    public static LiveLogger create(String tag) {
        return new LiveLogger(tag);
    }

    public static LiveLogger create(Class c) {
        return new LiveLogger(c.getSimpleName());
    }

    public void d(String msg) {
        if (DEBUG) {
            dOnAll(msg);
        }
    }

    public void i(String msg) {
        if (DEBUG) {
            iOnAll(msg);
        }
    }

    public void w(String msg) {
        if (DEBUG) {
            wOnAll(msg);
        }
    }

    public void e(String msg) {
        if (DEBUG) {
            eOnAll(msg);
        }
    }

    public void dOnAll(String msg) {
        Log.d(TAG, msg);
    }

    public void iOnAll(String msg) {
        Log.i(TAG, msg);
    }

    public void wOnAll(String msg) {
        Log.w(TAG, msg);
    }

    public void eOnAll(String msg) {
        Log.e(TAG, msg);
    }
}
