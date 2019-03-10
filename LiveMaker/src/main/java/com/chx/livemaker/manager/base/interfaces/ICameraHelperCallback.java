package com.chx.livemaker.manager.base.interfaces;

import android.hardware.camera2.CameraDevice;
import android.view.Surface;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/20  18:01
 */
public interface ICameraHelperCallback {

    void downLevel();

    ISurfaceHelper getSurfaceHelper();

    void checkSizeParams();

    void canRecorder();

    void onCaptureStarted(String templateType);

    void onCaptureProgressed(String templateType);

    void onCaptureCompleted(String templateType);

    void onDisconnected(CameraDevice camera);

    void onError(int errorCode);
}
