package com.chx.livemaker.callback;

import android.hardware.Camera;
import android.util.Size;

import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.media.recorder.RecorderParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/14  18:24
 */
public interface MediaRecorderCallback {

    void checkParams(RecorderParams params);

    void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams);

    void checkVideoSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams);

    void doStart(String from, String type);

    void doLoading(String from, String type, long progress);

    void doFinish(String from, String type);

    void onDisconnected();

    /**
     * @param errorMsg  error of message
     * @param errorCode error of code.
     * @see com.chx.livemaker.manager.media.MediaManager
     */
    void onCameraError(int errorCode, String errorMsg);

    void onMediaError(int what, int extra, String errorMsg);
}
