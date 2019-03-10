package com.chx.livemaker.manager.media.recorder;

import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.StringRes;

import com.chx.livemaker.callback.MediaRecorderCallback;
import com.chx.livemaker.manager.base.helpr.Camera1Helper;
import com.chx.livemaker.manager.base.helpr.Camera2Helper;
import com.chx.livemaker.manager.base.helpr.MediaRecorderHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelper;
import com.chx.livemaker.manager.base.interfaces.IMediaHelper;
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
 * on 2018/12/14  16:19
 */
public class RecorderParams extends BaseParams {

    private boolean isError = false;
    private boolean isRecord = false;

    private IMediaHelper mediaHelper;
    private ISurfaceHelper surfaceHelper;
    private ICameraHelper cameraHelper;

    private String faceType;

    private MediaRecorderCallback mediaRecorderCallback = new MediaRecorderCallbackEmptyImpl();

    RecorderParams() {
        setFaceType(MediaManager.FACE_BACK);
        setType(BaseManager.TYPE_VIDEO_RECORD);

        setSizeParams(new SizeParams());

        setCharacteristicsInfos(new ArrayList<CameraCharacteristicsInfo>());

        setMediaParams(new MediaParams());
        getMediaParams().setAudioSource(MediaRecorder.AudioSource.MIC);
        getMediaParams().setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        getMediaParams().setOutputPath("");
        getMediaParams().setVideoEncodingBitRate(10000000);
        getMediaParams().setVideoFrameRate(30);
        getMediaParams().setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        getMediaParams().setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        setMediaHelper(new MediaRecorderHelper());

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

        initCameraParams();
    }

    public String getFaceType() {
        return faceType;
    }

    public void setFaceType(String faceType) {
        this.faceType = faceType;
    }

    ICameraHelper getCameraHelper() {
        return cameraHelper;
    }

    public void setCameraHelper(ICameraHelper cameraHelper) {
        this.cameraHelper = cameraHelper;
    }

    IMediaHelper getMediaHelper() {
        return mediaHelper;
    }

    public void setMediaHelper(IMediaHelper mediaHelper) {
        this.mediaHelper = mediaHelper;
    }

    boolean isRecord() {
        return isRecord;
    }

    void setRecord(boolean record) {
        isRecord = record;
    }

    public ISurfaceHelper getSurfaceHelper() {
        return surfaceHelper;
    }

    public void setSurfaceHelper(ISurfaceHelper surfaceHelper) {
        this.surfaceHelper = surfaceHelper;
    }

    boolean isError() {
        return isError;
    }

    void setError(boolean error) {
        isError = error;
    }

    MediaRecorderCallback getMediaRecorderCallback() {
        return mediaRecorderCallback;
    }

    void setMediaRecorderCallback(MediaRecorderCallback mediaRecorderCallback) {
        this.mediaRecorderCallback = mediaRecorderCallback;
    }

    /*************************************额外提供功能性支持********************************************************/

    void initCameraParams() {
        if (cameraHelper instanceof Camera1Helper) {
            getMediaParams().setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (faceType.equals(MediaManager.FACE_FRONT)) {
                getCameraCharacteristicsUse().setLensFacingType(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                getCameraCharacteristicsUse().setLensFacingType(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } else if (cameraHelper instanceof Camera2Helper) {
            getCameraCharacteristicsUse().setFocusMode(CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            getCameraCharacteristicsUse().setFlashMode(CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            getMediaParams().setVideoSource(MediaRecorder.VideoSource.SURFACE);
            if (faceType.equals(MediaManager.FACE_FRONT)) {
                getCameraCharacteristicsUse().setLensFacingType(CameraCharacteristics.LENS_FACING_FRONT);
            } else {
                getCameraCharacteristicsUse().setLensFacingType(CameraCharacteristics.LENS_FACING_BACK);
            }
        } else {
            getCameraCharacteristicsUse().setFocusMode(0);
            getCameraCharacteristicsUse().setFlashMode(0);
        }
    }


    String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    @Override
    public void stop() {
        if (mediaHelper != null) {
            mediaHelper.onFinish();
        }
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
        if (mediaHelper != null) {
            mediaHelper.onDestroy();
            mediaHelper = null;
        }
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
