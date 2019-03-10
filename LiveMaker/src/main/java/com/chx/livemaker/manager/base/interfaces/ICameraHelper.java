package com.chx.livemaker.manager.base.interfaces;

import android.hardware.Camera;
import android.view.Surface;

import com.chx.livemaker.manager.base.params.BaseParams;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/20  17:15
 */
public interface ICameraHelper {

    void onCreate(BaseParams baseParams);

    void setHelperCallback(ICameraHelperCallback cameraHelperCallback);

    boolean isReady();

    Camera getCamera();

    void changeLensFacing(String faceType);

    void onStartPreview(List<Surface> surfaces);

    void onStartRecord(List<Surface> surfaces);

    void onStartCapture(List<Surface> surfaces);

    void onStop();

    void onResume();

    void onDestroy();

}
