package com.chx.livemaker.manager.base.interfaces;

import android.hardware.Camera;

import com.chx.livemaker.manager.base.params.LiveSize;

/**
 * Created by cangHX
 * on 2018/12/24  14:36
 */
public interface IMediaHelperCallback {

    void onMediaCreate();

    LiveSize getViewSize();

    Camera getCamera();

    void onStart();

    void onLoading(long timeMillis);

    void onFinish();

    void onFileSuccess();

    void onFileFailed();

    void onInfo(int what, int extra);

    void onError(int what, int extra, String msg);

}
