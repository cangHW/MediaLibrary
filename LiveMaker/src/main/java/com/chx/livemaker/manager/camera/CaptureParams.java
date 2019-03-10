package com.chx.livemaker.manager.camera;

import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.chx.livemaker.callback.CameraCaptureCallback;
import com.chx.livemaker.manager.base.helpr.Camera1Helper;
import com.chx.livemaker.manager.base.helpr.Camera2Helper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelper;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelper;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsInfo;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsUse;
import com.chx.livemaker.manager.base.params.MediaParams;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.media.MediaManager;

import java.util.ArrayList;

/**
 * Created by cangHX
 * on 2019/01/07  19:10
 */
public class CaptureParams extends BaseParams {

    private boolean isError = false;
    private boolean isCapture = false;

    private ICameraHelper cameraHelper;
    private ISurfaceHelper surfaceHelper;

    private String faceType;

    private CameraCaptureCallback cameraCaptureCallback = new CameraCaptureCallbackEmptyImpl();

    CaptureParams() {
        setFaceType(MediaManager.FACE_BACK);
        setType(BaseManager.TYPE_PICTURE);

        setSizeParams(new SizeParams());

        setCharacteristicsInfos(new ArrayList<CameraCharacteristicsInfo>());

        setCameraCharacteristicsUse(new CameraCharacteristicsUse());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setCameraHelper(new Camera1Helper());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setCameraHelper(new Camera2Helper());
        } else {
            //TODO Camera2Api23Helper
            setCameraHelper(new Camera2Helper());
//            setCameraHelper(new Camera2Api23Helper());
        }
        setMediaParams(new MediaParams());
        initCameraParams();
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public ICameraHelper getCameraHelper() {
        return cameraHelper;
    }

    public void setCameraHelper(ICameraHelper cameraHelper) {
        this.cameraHelper = cameraHelper;
    }

    public ISurfaceHelper getSurfaceHelper() {
        return surfaceHelper;
    }

    public void setSurfaceHelper(ISurfaceHelper surfaceHelper) {
        this.surfaceHelper = surfaceHelper;
    }

    public CameraCaptureCallback getCameraCaptureCallback() {
        return cameraCaptureCallback;
    }

    public void setCameraCaptureCallback(CameraCaptureCallback cameraCaptureCallback) {
        this.cameraCaptureCallback = cameraCaptureCallback;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getFaceType() {
        return faceType;
    }

    public void setFaceType(String faceType) {
        this.faceType = faceType;
    }

    /**********************************************************************************************/

    void initCameraParams(){
        if (cameraHelper instanceof Camera1Helper) {
            if (faceType.equals(MediaManager.FACE_FRONT)){
                getCameraCharacteristicsUse().setLensFacingType(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }else {
                getCameraCharacteristicsUse().setLensFacingType(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        }else if (cameraHelper instanceof Camera2Helper){
            getCameraCharacteristicsUse().setFocusMode(CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            getCameraCharacteristicsUse().setFlashMode(CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            if (faceType.equals(MediaManager.FACE_FRONT)){
                getCameraCharacteristicsUse().setLensFacingType(CameraCharacteristics.LENS_FACING_FRONT);
            }else {
                getCameraCharacteristicsUse().setLensFacingType(CameraCharacteristics.LENS_FACING_BACK);
            }
        }else {
            getCameraCharacteristicsUse().setFocusMode(0);
            getCameraCharacteristicsUse().setFlashMode(0);
        }
    }

    @Override
    public void stop() {
        if (cameraHelper != null) {
            cameraHelper.onStop();
        }
    }

    @Override
    public void resume() {
        if (cameraHelper != null) {
            cameraHelper.onResume();
        }
    }

    @Override
    public void destroyToCleanMemory() {
        if (surfaceHelper != null) {
            surfaceHelper.unHelper();
            surfaceHelper = null;
        }
        if (cameraHelper != null) {
            cameraHelper.onDestroy();
            cameraHelper = null;
        }
    }
}
