package com.chx.livemaker.manager.base.helpr;

import android.hardware.Camera;
import android.view.Surface;

import com.chx.livemaker.manager.base.interfaces.ICameraHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelperCallback;
import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.media.recorder.RecorderParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/25  17:29
 */
public class Camera2Api23Helper implements ICameraHelper {
    @Override
    public void onCreate(BaseParams baseParams) {

    }

    @Override
    public void setHelperCallback(ICameraHelperCallback cameraHelperCallback) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public Camera getCamera() {
        return null;
    }

    @Override
    public void changeLensFacing(String faceType) {

    }

    @Override
    public void onStartPreview(List<Surface> surfaces) {

    }

    @Override
    public void onStartRecord(List<Surface> surfaces) {

    }

    @Override
    public void onStartCapture(List<Surface> surfaces) {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
