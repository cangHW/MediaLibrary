package com.chx.livemaker.manager.base.helpr;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.chx.livemaker.R;
import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelper;
import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelperCallback;
import com.chx.livemaker.manager.base.interfaces.ICameraHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelperCallback;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsInfo;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsUse;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.media.MediaManager;
import com.chx.livemaker.manager.thread.ThreadManager;
import com.chx.livemaker.util.SupportAutoFocusManager;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.util.SupportPictureUtil;
import com.chx.livemaker.util.SupportRotationManager;
import com.chx.livemaker.util.SupportSizeManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/25  17:19
 */
public class Camera1Helper implements ICameraHelper {

    private static final int FOCUS_COUNT_DEFAULT = 0;
    //当对焦失败时，最大重复尝试三次对焦
    private static final int FOCUS_COUNT_MAX = 3;
    private static final LiveLogger mLogger = LiveLogger.create(Camera1Helper.class);
    private static final IAutoFocusHelper mAutoFocusHelper = Camera1AutoFocusHelper.getInstance();

    //是否初始化完成
    private boolean isReady = false;
    //是否录像中
    private boolean isVideo = false;
    //是否处于暂停状态
    private boolean isStop = false;
    //是否启用闪光灯
    private boolean isFlash = false;
    //是否是auto方式对焦
    private boolean isAutoFocus = false;
    //是否手动对焦
    private boolean isTouchFocus = false;
    //对焦次数
    private int mFocusCount = FOCUS_COUNT_DEFAULT;

    //操作类型
    private String mType;

    private Activity mActivity;
    private BaseParams mBaseParams;
    //当前选择摄像头支持的参数
    private CameraCharacteristicsInfo mCharacteristicsInfo;
    //用户设置的参数
    private CameraCharacteristicsUse mCharacteristicsUse;
    //回调
    private ICameraHelperCallback mCameraHelperCallback;

    private Camera mCamera;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(BaseParams baseParams) {
        mFocusCount = FOCUS_COUNT_DEFAULT;
        mType = baseParams.getType();
        mActivity = (Activity) baseParams.getContext();
        mBaseParams = baseParams;
        mCharacteristicsUse = baseParams.getCameraCharacteristicsUse();
        try {
            checkCameraInfo(baseParams);
            mCharacteristicsInfo = baseParams.getCameraCharacteristicsInfo();
            initCamera();
            mAutoFocusHelper.setAutoFocusCallback(mFocusHelperCallback);
            mAutoFocusHelper.onStart();
        } catch (Exception e) {
            if ("Fail to connect to camera service".equals(e.getMessage())) {
                mLogger.dOnAll(mActivity.getString(R.string.no_camera_permission));
            } else if ("Camera initialization failed".equals(e.getMessage())) {
                mLogger.dOnAll(mActivity.getString(R.string.camera_initialize_failed));
            } else {
                mLogger.dOnAll(mActivity.getString(R.string.unKnown));
            }
            return;
        }
        mLogger.i("onCreate");
    }

    @Override
    public void setHelperCallback(ICameraHelperCallback cameraHelperCallback) {
        this.mCameraHelperCallback = cameraHelperCallback;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public void changeLensFacing(String faceType) {
        if (faceType.equals(MediaManager.FACE_FRONT)) {
            mCharacteristicsUse.setLensFacingType(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            mCharacteristicsUse.setLensFacingType(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        mCharacteristicsInfo = mBaseParams.getCameraCharacteristicsInfo();
        try {
            releaseCamera();
            initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLogger.i("changeLensFacing  faceType : " + faceType);
    }

    @Override
    public void onStartPreview(List<Surface> surfaces) {
        isFlash = false;
        isVideo = false;
    }

    @Override
    public void onStartRecord(List<Surface> surfaces) {
        isFlash = false;
        isVideo = true;
        mCameraHelperCallback.canRecorder();
    }

    @Override
    public void onStartCapture(List<Surface> surfaces) {
        isFlash = true;
        isVideo = false;
        mCameraHelperCallback.onCaptureStarted(BaseManager.TEMPLATE_STILL_CAPTURE);
        mCamera.takePicture(mShutterCallback, mRawPictureCallback, mPostViewPictureCallback, mJpegPictureCallback);
    }

    @Override
    public void onStop() {
        releaseCamera();
        mLogger.i("onLifecycleStop");
    }

    @Override
    public void onResume() {
        if (isStop) {
            try {
                createCamera();
                openCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isStop = false;
        isVideo = false;
        mLogger.i("onLifecycleResume");
    }

    @Override
    public void onDestroy() {
        releaseCamera();
        mHandler = null;
        mAutoFocusHelper.onFinished();
        mLogger.i("onLifecycleDestroy");
    }

    private void releaseCamera() {
        mHandler.removeCallbacksAndMessages(null);
        try {
            isReady = false;
            isStop = true;
            isVideo = false;
            mCamera.setOneShotPreviewCallback(null);
            mCamera.cancelAutoFocus();
        } catch (Exception e) {
            e.toString();
        }
        try {
            mCamera.stopPreview();
            mCamera.release();
        } catch (Exception e) {
            e.toString();
        } finally {
            mCamera = null;
        }
    }

    private void initCamera() throws IOException {
        createCamera();
        checkCameraParams();
        openCamera();
        startAutoFocus();
    }

    private void openCamera() {
        Camera.Parameters parameters = mCamera.getParameters();
        SizeParams sizeParams = mBaseParams.getSizeParams();
        //设置照片尺寸
        if (sizeParams.getPictureSize() == null) {
            LiveSize size = SupportSizeManager.getInstance().getPictureSize(mCameraHelperCallback.getSurfaceHelper().getViewSize(), mCharacteristicsInfo.getPictureSizes(), mCharacteristicsInfo.getPictureSizeList());
            sizeParams.setPictureSize(size);
        }
        parameters.setPictureSize(sizeParams.getPictureSize().getWidth(), sizeParams.getPictureSize().getHeight());
        //设置预览尺寸
        if (sizeParams.getPreviewSize() == null) {
            LiveSize size = SupportSizeManager.getInstance().getPictureSize(mCameraHelperCallback.getSurfaceHelper().getViewSize(), mCharacteristicsInfo.getPreviewSizes(), mCharacteristicsInfo.getPreviewSizeList());
            sizeParams.setPreviewSize(size);
        }
        parameters.setPreviewSize(sizeParams.getPreviewSize().getWidth(), sizeParams.getPreviewSize().getHeight());
        //设置闪光灯
        if (isFlash) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        mCamera.setParameters(parameters);
        setFocusMode();
        mCamera.setDisplayOrientation(SupportRotationManager.getInstance().getRotation(mActivity, mCharacteristicsInfo.getLensFacingType(), mCharacteristicsInfo.getOrientation()));
        mCamera.startPreview();
    }

    //设置对焦
    private void setFocusMode() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModesList = mCharacteristicsInfo.getFocusModesList();
        if (BaseManager.TYPE_VIDEO_RECORD.equals(mType) && SupportAutoFocusManager.getInstance().isSupportVideoMode(focusModesList)) {
            isAutoFocus = false;
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (BaseManager.TYPE_PICTURE.equals(mType) && SupportAutoFocusManager.getInstance().isSupportPictureMode(focusModesList)) {
            isAutoFocus = false;
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            isAutoFocus = true;
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(parameters);
    }

    private void checkCameraParams() {
        Camera.Parameters parameters = mCamera.getParameters();
        setCameraParamsCache(parameters);
        mCameraHelperCallback.checkSizeParams();
    }

    private void setCameraParamsCache(Camera.Parameters parameters) {
        mCharacteristicsInfo.setMaxNumFocusAreas(parameters.getMaxNumFocusAreas());
        mCharacteristicsInfo.setMaxNumMeteringAreas(parameters.getMaxNumMeteringAreas());
        List<String> focusModes = parameters.getSupportedFocusModes();
        mCharacteristicsInfo.setFocusModesList(focusModes);
        mCharacteristicsInfo.setFocusAvailable(focusModes != null && focusModes.size() != 0);
        List<String> colorEffects = parameters.getSupportedColorEffects();
        mCharacteristicsInfo.setColorEffectList(colorEffects);
        mCharacteristicsInfo.setColorEffectsAvailable(colorEffects != null && colorEffects.size() != 0);
        List<String> flashModes = parameters.getSupportedFlashModes();
        mCharacteristicsInfo.setFlashModeList(flashModes);
        mCharacteristicsInfo.setFlashAvailable(flashModes != null && flashModes.size() != 0);
        List<String> sceneModes = parameters.getSupportedSceneModes();
        mCharacteristicsInfo.setSceneModeList(sceneModes);
        mCharacteristicsInfo.setSceneAvailable(sceneModes != null && sceneModes.size() != 0);
        List<Camera.Size> jpegThumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
        mCharacteristicsInfo.setJpegThumbnailSizeList(jpegThumbnailSizes);
        mCharacteristicsInfo.setJpegThumbnailSizesAvailable(jpegThumbnailSizes != null && jpegThumbnailSizes.size() != 0);
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        mCharacteristicsInfo.setPictureSizeList(pictureSizes);
        mCharacteristicsInfo.setPictureSizesAvailable(pictureSizes != null && pictureSizes.size() != 0);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        mCharacteristicsInfo.setPreviewSizeList(previewSizes);
        mCharacteristicsInfo.setPreviewSizesAvailable(previewSizes != null && previewSizes.size() != 0);
        List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
        mCharacteristicsInfo.setVideoSizeList(videoSizes);
        mCharacteristicsInfo.setVideoSizesAvailable(videoSizes != null && videoSizes.size() != 0);
    }

    private void checkCameraInfo(BaseParams recorderParams) {
        int cameraNumber = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNumber; i++) {
            try {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    if (mCharacteristicsUse.getLensFacingType() == -1) {
                        mCharacteristicsUse.setLensFacingType(cameraInfo.facing);
                    }
                }

                CameraCharacteristicsInfo params = new CameraCharacteristicsInfo();
                params.setCameraId(String.valueOf(i));
                params.setLensFacingType(cameraInfo.facing);
                params.setOrientation(cameraInfo.orientation);
                params.setCanDisableShutterSound(cameraInfo.canDisableShutterSound);
                recorderParams.addCameraCharacteristicsInfo(params);
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    private void createCamera() throws IOException {
        mCamera = Camera.open(mCharacteristicsUse.getLensFacingType());
        SurfaceHolder holder = mCameraHelperCallback.getSurfaceHelper().getSurfaceHolder();
        if (holder != null) {
            mCamera.setPreviewDisplay(holder);
        }
        SurfaceTexture texture = mCameraHelperCallback.getSurfaceHelper().getSurfaceTexture();
        if (texture != null) {
            mCamera.setPreviewTexture(texture);
        }
//        mCamera.autoFocus(mAutoFocusCallback);
        mCamera.setAutoFocusMoveCallback(mAutoFocusMoveCallback);
        mCamera.setErrorCallback(mErrorCallback);
        mCamera.setFaceDetectionListener(mFaceDetectionListener);
        mCamera.setOneShotPreviewCallback(mOneShotPreviewCallback);
//        mCamera.setPreviewCallback(mPreviewCallback);
//        mCamera.setPreviewCallbackWithBuffer(mPreviewWithBufferCallback);
        mCamera.setZoomChangeListener(mZoomChangeListener);
    }

    private void startAutoFocus() {
        if (!isAutoFocus) {
            return;
        }
        //auto方法需要在startPreview方法之后，stopPreview方法之前执行
        mHandler.removeCallbacksAndMessages(null);
        mCamera.cancelAutoFocus();
        //延迟200毫秒，启动对焦
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCamera.autoFocus(mAutoFocusCallback);
            }
        }, 200);
    }

    // 触摸对焦
    private void startTouchFocus(Rect focusArea, Rect meteringArea) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (focusArea != null) {
            parameters.setFocusAreas(Collections.singletonList(new Camera.Area(focusArea, IAutoFocusHelper.FOCUS_AREA_WEIGHT)));
        }
        if (meteringArea != null) {
            parameters.setMeteringAreas(Collections.singletonList(new Camera.Area(meteringArea, IAutoFocusHelper.FOCUS_AREA_WEIGHT)));
        }
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.toString();
        }
        isAutoFocus = true;
        isTouchFocus = true;
        startAutoFocus();
    }

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mLogger.i("onAutoFocus  success ? == " + success);
            mCamera.setOneShotPreviewCallback(null);
            boolean isCanResetFocusMode = false;

            if (isAutoFocus) {
                mCamera.cancelAutoFocus();
                if (!success) {
                    if (mFocusCount < FOCUS_COUNT_MAX) {
                        //对焦失败，重新启动对焦
                        startAutoFocus();
                    } else {
                        isCanResetFocusMode = true;
                    }
                    mFocusCount++;
                }
            } else {
                mFocusCount = FOCUS_COUNT_DEFAULT;
                isCanResetFocusMode = true;
            }

            if (isCanResetFocusMode) {
                mAutoFocusHelper.unLock();
                if (isTouchFocus) {
                    isTouchFocus = false;
                    setFocusMode();
                }
            }
        }
    };

    private Camera.AutoFocusMoveCallback mAutoFocusMoveCallback = new Camera.AutoFocusMoveCallback() {
        @Override
        public void onAutoFocusMoving(boolean start, Camera camera) {
            mLogger.i("onAutoFocusMoving");
        }
    };

    private Camera.ErrorCallback mErrorCallback = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            mLogger.i("onError,errorCode: " + error);
        }
    };

    private Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            mLogger.i("onFaceDetection");
        }
    };

    private Camera.PreviewCallback mOneShotPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isReady = true;
            mCameraHelperCallback.canRecorder();
            mLogger.i("onPreviewFrame  OneShot");
        }
    };

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isReady = true;
            mCameraHelperCallback.canRecorder();
            mLogger.i("onPreviewFrame");
        }
    };

    private Camera.PreviewCallback mPreviewWithBufferCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isReady = true;
            mCameraHelperCallback.canRecorder();
            mLogger.i("onPreviewFrame   Buffer");
        }
    };

    private Camera.PictureCallback mRawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mLogger.i("onPictureTaken   Raw");
        }
    };

    private Camera.PictureCallback mPostViewPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mLogger.i("onPictureTaken   Post");
        }
    };

    private Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            mLogger.i("onPictureTaken   Jpeg");
            isFlash = false;
            mCamera.cancelAutoFocus();
            openCamera();
            mCameraHelperCallback.onCaptureProgressed(BaseManager.TEMPLATE_STILL_CAPTURE);
            ThreadManager.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    int rotation = SupportRotationManager.getInstance().getRotation(mActivity, mCharacteristicsUse.getLensFacingType(), mCharacteristicsInfo.getOrientation());
                    String path = mBaseParams.getMediaParams().getOutputPath();
                    boolean isSuccess = SupportPictureUtil.savePicture(data, rotation, path);
                    if (isSuccess) {
                        mCameraHelperCallback.onCaptureCompleted(BaseManager.TEMPLATE_STILL_CAPTURE);
                    } else {
                        mCameraHelperCallback.onError(BaseManager.ERROR_STILL_CAPTURE_FAILED);
                    }
                }
            });
        }
    };

    private Camera.OnZoomChangeListener mZoomChangeListener = new Camera.OnZoomChangeListener() {
        @Override
        public void onZoomChange(int zoomValue, boolean stopped, Camera camera) {
            mLogger.i("onZoomChange");
        }
    };

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mLogger.i("onShutter");
        }
    };

    private IAutoFocusHelperCallback mFocusHelperCallback = new IAutoFocusHelperCallback() {
        @Override
        public void onAutoFocus() {
            if (!isReady) {
                mLogger.i("loading");
                return;
            }
            if (isVideo) {
                mLogger.i("can not auto focus,when video");
                return;
            }
            mFocusCount = FOCUS_COUNT_DEFAULT;
            startAutoFocus();
        }

        @Override
        public void onTouchFocus(int x, int y) {
            if (!isReady) {
                mLogger.i("loading");
                return;
            }
            if (isVideo) {
                //录像中无法触摸对焦
                mLogger.i("can not touch focus,when video");
                return;
            }
            if (mCharacteristicsInfo.getMaxNumFocusAreas() <= 0 && mCharacteristicsInfo.getMaxNumMeteringAreas() <= 0) {
                //不支持触摸对焦
                mLogger.i("not support touch focus");
                return;
            }
            LiveSize liveSize = mCameraHelperCallback.getSurfaceHelper().getViewSize();
            Rect focusArea = null;
            if (mCharacteristicsInfo.getMaxNumFocusAreas() > 0) {
                focusArea = mAutoFocusHelper.measureAreaForFocus(mActivity, x, y, 1f, liveSize, mCharacteristicsUse.getLensFacingType(), mCharacteristicsInfo.getOrientation());
            }
            Rect meteringArea = null;
            if (mCharacteristicsInfo.getMaxNumMeteringAreas() > 0) {
                meteringArea = mAutoFocusHelper.measureAreaForFocus(mActivity, x, y, 2f, liveSize, mCharacteristicsUse.getLensFacingType(), mCharacteristicsInfo.getOrientation());
            }
            mFocusCount = FOCUS_COUNT_DEFAULT;
            startTouchFocus(focusArea, meteringArea);
        }
    };
}
