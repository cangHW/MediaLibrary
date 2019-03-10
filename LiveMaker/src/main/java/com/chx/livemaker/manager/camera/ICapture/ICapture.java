package com.chx.livemaker.manager.camera.ICapture;

import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.TextureView;

import com.chx.livemaker.callback.CameraCaptureCallback;
import com.chx.livemaker.manager.base.interfaces.ILifecycleInterface;

/**
 * Created by cangHX
 * on 2019/01/07  19:06
 */
public interface ICapture extends ILifecycleInterface {

    ICapture setView(@NonNull SurfaceView surfaceView);

    ICapture setView(@NonNull TextureView textureView);

    ICapture setLifecycleEnable(boolean enable);

    ICapture setIsLevelCanDown(boolean isLevelCanDown);

    ICapture setLensFacing(@NonNull String faceType);

    ICapture setFilePath(@NonNull String path);

    ICapture setCaptureCallback(@NonNull CameraCaptureCallback captureCallback);

    void initialize();

    String getFilePath();

    void changeLensFacing(@NonNull String faceType);

    void takeCapture();
}
