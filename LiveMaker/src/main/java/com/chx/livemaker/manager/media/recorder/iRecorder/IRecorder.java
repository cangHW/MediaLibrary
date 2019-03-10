package com.chx.livemaker.manager.media.recorder.iRecorder;

import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.TextureView;

import com.chx.livemaker.callback.MediaRecorderCallback;
import com.chx.livemaker.manager.base.interfaces.ILifecycleInterface;

/**
 * Created by cangHX
 * on 2018/12/14  14:43
 */
public interface IRecorder extends ILifecycleInterface {

    IRecorder setView(@NonNull SurfaceView surfaceView);

    IRecorder setView(@NonNull TextureView textureView);

    IRecorder setLifecycleEnable(boolean enable);

    IRecorder setIsLevelCanDown(boolean isLevelCanDown);

    IRecorder setLensFacing(@NonNull String faceType);

    IRecorder setFilePath(@NonNull String filePath);

    IRecorder setMediaRecordCallback(@NonNull MediaRecorderCallback mediaRecorderCallback);

    void initialize();

    String getFilePath();

    void changeLensFacing(@NonNull String faceType);

    void onStartRecord();

    void onStopRecord();

    void onResumeRecord();

    void onFinishRecord();

    void reset();
}
