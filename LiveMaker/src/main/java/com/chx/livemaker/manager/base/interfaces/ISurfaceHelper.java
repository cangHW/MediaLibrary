package com.chx.livemaker.manager.base.interfaces;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.chx.livemaker.manager.base.params.LiveSize;

/**
 * Created by cangHX
 * on 2018/12/14  16:06
 */
public interface ISurfaceHelper {

    void helper();

    void unHelper();

    void setHelperCallback(ISurfaceHelperCallback helperCallback);

    Size[] getSupportSizes(StreamConfigurationMap map);

    Surface getSurface();

    SurfaceHolder getSurfaceHolder();

    SurfaceTexture getSurfaceTexture();

    LiveSize getViewSize();

    long getDelayedTime();

}
