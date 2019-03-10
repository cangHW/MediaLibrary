package com.chx.livemaker.application;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by cangHX
 * on 2019/01/10  15:56
 */
public class LiveApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "dcafe05f48", false);
    }
}
