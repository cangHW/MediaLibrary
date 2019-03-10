package com.chx.livemaker.manager.camera;

import android.hardware.Camera;
import android.util.Size;

import com.chx.livemaker.callback.CameraCaptureCallback;
import com.chx.livemaker.manager.base.params.SizeParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2019/01/08  10:09
 */
public class CameraCaptureCallbackEmptyImpl implements CameraCaptureCallback {
    @Override
    public void checkParams(CaptureParams params) {

    }

    @Override
    public void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {

    }

    @Override
    public void checkPictureSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {

    }

    @Override
    public void doStart() {

    }

    @Override
    public void doLoading() {

    }

    @Override
    public void doFinish() {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }
}
