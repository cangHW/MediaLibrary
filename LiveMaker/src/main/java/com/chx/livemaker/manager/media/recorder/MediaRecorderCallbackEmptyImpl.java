package com.chx.livemaker.manager.media.recorder;

import android.hardware.Camera;
import android.util.Size;

import com.chx.livemaker.callback.MediaRecorderCallback;
import com.chx.livemaker.manager.base.params.SizeParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/14  18:31
 */
public class MediaRecorderCallbackEmptyImpl implements MediaRecorderCallback {
    @Override
    public void checkParams(RecorderParams params) {

    }

    @Override
    public void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {

    }

    @Override
    public void checkVideoSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {

    }

    @Override
    public void doStart(String from, String type) {

    }

    @Override
    public void doLoading(String from, String type, long progress) {

    }

    @Override
    public void doFinish(String from, String type) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onCameraError(int errorCode, String errorMsg) {

    }

    @Override
    public void onMediaError(int what, int extra, String errorMsg) {

    }
}
