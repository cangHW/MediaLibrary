package com.chx.livemaker.callback;

import android.hardware.Camera;
import android.util.Size;

import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.camera.CaptureParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2019/01/08  10:09
 */
public interface CameraCaptureCallback {

    void checkParams(CaptureParams params);

    void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams);

    void checkPictureSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams);

    void doStart();

    void doLoading();

    void doFinish();

    void onError(int errorCode, String errorMsg);
}
