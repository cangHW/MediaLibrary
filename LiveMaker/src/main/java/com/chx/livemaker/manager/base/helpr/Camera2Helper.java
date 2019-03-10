package com.chx.livemaker.manager.base.helpr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelper;
import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelperCallback;
import com.chx.livemaker.manager.base.interfaces.ICameraHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelperCallback;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsInfo;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsUse;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.manager.media.MediaManager;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.util.SupportAutoFocusManager;
import com.chx.livemaker.util.SupportSizeManager;
import com.chx.livemaker.util.SupportPictureUtil;
import com.chx.livemaker.util.SupportRotationManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/20  17:22
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Helper implements ICameraHelper {

    private static final int FOCUS_COUNT_DEFAULT = 0;
    //当对焦失败时，最大重复尝试三次对焦
    private static final int FOCUS_COUNT_MAX = 3;
    private static final LiveLogger mLogger = LiveLogger.create(Camera2Helper.class);
    private static final IAutoFocusHelper mAutoFocusHelper = Camera2AutoFocusHelper.getInstance();

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

    private String mTemplateType;

    //操作类型
    private String mType;

    private BaseParams mBaseParams;
    private CameraCharacteristicsInfo mCharacteristicsInfo;
    private CameraCharacteristicsUse mCharacteristicsUse;
    private ICameraHelperCallback mCameraHelperCallback;

    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private ImageReader mImageReader;

    private List<Surface> mSurfaces;

    @Override
    public void onCreate(BaseParams baseParams) {
        if (ActivityCompat.checkSelfPermission(baseParams.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CameraManager cameraManager = (CameraManager) baseParams.getContext().getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager == null) {
            throw new NullPointerException("the camera can not to use");
        }
        mFocusCount = FOCUS_COUNT_DEFAULT;
        this.mBaseParams = baseParams;
        this.mCameraManager = cameraManager;
        this.mType = baseParams.getType();
        this.mCharacteristicsUse = baseParams.getCameraCharacteristicsUse();
        checkCameraParams(mCameraManager, baseParams);
        mCameraHelperCallback.checkSizeParams();
        this.mHandlerThread = new HandlerThread("camera");
        this.mHandlerThread.start();
        this.mBackgroundHandler = new Handler(mHandlerThread.getLooper());
        openCamera(mCameraManager);
        mAutoFocusHelper.setAutoFocusCallback(mFocusHelperCallback);
        mAutoFocusHelper.onStart();
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
        return null;
    }

    @Override
    public void changeLensFacing(String faceType) {
        closeCaptureSession();
        closeCameraDevice();
        if (faceType.equals(MediaManager.FACE_FRONT)) {
            mCharacteristicsUse.setLensFacingType(CameraCharacteristics.LENS_FACING_FRONT);
        } else {
            mCharacteristicsUse.setLensFacingType(CameraCharacteristics.LENS_FACING_BACK);
        }
        openCamera(mCameraManager);
    }

    @Override
    public void onStartPreview(List<Surface> surfaces) {
        isFlash = false;
        isVideo = false;
        mSurfaces = surfaces;
        mTemplateType = BaseManager.TEMPLATE_PREVIEW;
        closeCaptureSession();
        checkIsReadyToCreateSession(surfaces, CameraDevice.TEMPLATE_PREVIEW);
        isReady = false;
    }

    @Override
    public void onStartRecord(List<Surface> surfaces) {
        isFlash = false;
        isVideo = true;
        mTemplateType = BaseManager.TEMPLATE_RECORD;
        closeCaptureSession();
        checkIsReadyToCreateSession(surfaces, CameraDevice.TEMPLATE_RECORD);
        isReady = false;
    }

    @Override
    public void onStartCapture(List<Surface> surfaces) {
        if (BaseManager.TEMPLATE_RECORD.equals(mTemplateType)) {
            mLogger.dOnAll("please set recorder finish");
            return;
        }
        isFlash = true;
        isVideo = false;
        mCameraHelperCallback.onCaptureStarted(BaseManager.TEMPLATE_STILL_CAPTURE);
        mTemplateType = BaseManager.TEMPLATE_STILL_CAPTURE;
        surfaces.add(mImageReader.getSurface());
        CaptureRequest.Builder builder = createCaptureRequest(surfaces, CameraDevice.TEMPLATE_STILL_CAPTURE);
        startCapture(setModeToBuilder(builder));
        isReady = false;
    }

    @Override
    public void onStop() {
        isStop = true;
        closeCaptureSession();
        isReady = false;
    }

    @Override
    public void onResume() {
        if (isStop && mSurfaces != null) {
            mTemplateType = BaseManager.TEMPLATE_PREVIEW;
            checkIsReadyToCreateSession(mSurfaces, CameraDevice.TEMPLATE_PREVIEW);
        }
//        isCapture = false;
        isStop = false;
    }

    @Override
    public void onDestroy() {
        closeCaptureSession();
        closeCameraDevice();
        destroyThreadAndHandle();
        if (mImageReader != null) {
            mImageReader.close();
        }
        mAutoFocusHelper.onFinished();
        mCaptureRequestBuilder = null;
        mCameraManager = null;
    }

    private void closeCaptureSession() {
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.close();
                mCameraCaptureSession.abortCaptures();
            } catch (Exception e) {
                e.toString();
            } finally {
                mCameraCaptureSession = null;
            }
        }
    }

    private void closeCameraDevice() {
        if (mCameraDevice != null) {
            try {
                mCameraDevice.close();
            } catch (Exception e) {
                e.toString();
            } finally {
                mCameraDevice = null;
            }
        }
    }

    private void destroyThreadAndHandle() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
            try {
                mHandlerThread.join();
            } catch (Exception e) {
                e.toString();
            } finally {
                mHandlerThread = null;
            }
        }
        if (mBackgroundHandler != null) {
            try {
                mBackgroundHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.toString();
            } finally {
                mBackgroundHandler = null;
            }
        }
    }

    private void createImageReader() {
        if (mImageReader != null) {
            try {
                mImageReader.close();
            } catch (Exception e) {
                e.toString();
            } finally {
                mImageReader = null;
            }
        }
        LiveSize liveSize = mBaseParams.getSizeParams().getPictureSize();
        if (liveSize == null) {
            liveSize = SupportSizeManager.getInstance().getPictureSize(mCameraHelperCallback.getSurfaceHelper().getViewSize(), mCharacteristicsInfo.getPictureSizes(), null);
        }
        mImageReader = ImageReader.newInstance(liveSize.getWidth(), liveSize.getHeight(), ImageFormat.JPEG, 2);
        mImageReader.setOnImageAvailableListener(mImageAvailableListener, mBackgroundHandler);
    }

    private void checkIsReadyToCreateSession(final List<Surface> surfaces, final int templateType) {
        if (isReady) {
            mBackgroundHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCaptureRequestBuilder = createCaptureRequest(surfaces, templateType);
                    createCaptureSession(surfaces);
                }
            }, mCameraHelperCallback.getSurfaceHelper().getDelayedTime());
        } else {
            mCaptureRequestBuilder = createCaptureRequest(surfaces, templateType);
            createCaptureSession(surfaces);
        }
    }

    private CaptureRequest.Builder createCaptureRequest(List<Surface> surfaces, int templateType) {
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(templateType);
            for (Surface surface : surfaces) {
                builder.addTarget(surface);
            }
            return builder;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CaptureRequest.Builder setModeToBuilder(CaptureRequest.Builder builder) {
        if (BaseManager.TYPE_VIDEO_RECORD.equals(mTemplateType) && SupportAutoFocusManager.getInstance().isSupportVideoMode(mCharacteristicsInfo.getFocusModes())) {
            isAutoFocus = false;
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
        } else if (SupportAutoFocusManager.getInstance().isSupportPictureMode(mCharacteristicsInfo.getFocusModes())) {
            isAutoFocus = false;
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        } else {
            isAutoFocus = true;
            builder.set(CaptureRequest.CONTROL_MODE, CameraCharacteristics.CONTROL_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraCharacteristics.CONTROL_AF_MODE_AUTO);
        }

        if (mCharacteristicsInfo.isFlashAvailable()) {
            if (isFlash) {
                builder.set(CaptureRequest.CONTROL_AE_MODE, mCharacteristicsUse.getFlashMode());
            }
        }
        return builder;
    }

    private void createCaptureSession(List<Surface> surfaces) {
        try {
            ArrayList<Surface> arrayList = new ArrayList<>(surfaces);
            if (BaseManager.TYPE_PICTURE.equals(mType)) {
                createImageReader();
                arrayList.add(mImageReader.getSurface());
            }
            mCameraDevice.createCaptureSession(arrayList, mSessionStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkCameraParams(CameraManager cameraManager, BaseParams baseParams) {
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String cameraId : cameraIds) {
                CameraCharacteristicsInfo params = new CameraCharacteristicsInfo();
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                params.setSupportedHardwareLevel(level == null ? 0 : level);
                if (mBaseParams.isCanDownLevel() && params.getSupportedHardwareLevel() >= CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                    mCameraHelperCallback.downLevel();
                    return;
                }

                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == null) {
                    continue;
                }
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    if (mCharacteristicsUse.getLensFacingType() == -1) {
                        mCharacteristicsUse.setLensFacingType(facing);
                    }
                }
                params.setCameraId(cameraId);
                params.setLensFacingType(facing);

                Integer maxNumFocusAreas = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
                params.setMaxNumFocusAreas(maxNumFocusAreas == null ? 0 : maxNumFocusAreas);

                Integer maxNumMeteringAreas = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
                params.setMaxNumMeteringAreas(maxNumMeteringAreas == null ? 0 : maxNumMeteringAreas);

                Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                params.setArraySize(rect);

                Boolean flash_available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                params.setFlashAvailable(flash_available == null ? false : flash_available);

                int[] afs = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
                params.setFocusAvailable(afs != null && afs.length != 0);
                params.setFocusModes(afs);

                int[] effects = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
                params.setColorEffectsAvailable(effects != null && effects.length != 0);
                params.setEffectModes(effects);

                int[] scenes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
                params.setSceneAvailable(scenes != null && scenes.length != 0);
                params.setSceneModes(scenes);

                int[] videos = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
                params.setVideoStabilizationAvailable(videos != null && videos.length != 0);
                params.setVideoStabilizationModes(videos);

                int[] awbs = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
                params.setAwbModes(awbs);

                Integer orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                params.setOrientation(orientation == null ? 0 : orientation);

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                params.setVideoSizesAvailable(true);
                params.setVideoSizes(mCameraHelperCallback.getSurfaceHelper().getSupportSizes(map));
                params.setPreviewSizesAvailable(true);
                params.setPreviewSizes(mCameraHelperCallback.getSurfaceHelper().getSupportSizes(map));
                params.setPictureSizesAvailable(true);
                params.setPictureSizes(mCameraHelperCallback.getSurfaceHelper().getSupportSizes(map));

                baseParams.addCameraCharacteristicsInfo(params);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera(CameraManager cameraManager) {
        this.mCharacteristicsInfo = mBaseParams.getCameraCharacteristicsInfo();
        if (mCharacteristicsInfo != null) {
            try {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                cameraManager.openCamera(mCharacteristicsInfo.getCameraId(), mStateCallback, mainHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRequest(CaptureRequest.Builder builder) {
        if (builder == null) {
            return;
        }
        CaptureRequest request = builder.build();
        try {
            mCameraCaptureSession.setRepeatingRequest(request, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startCapture(CaptureRequest.Builder builder) {
        if (builder == null) {
            return;
        }
        CaptureRequest request = builder.build();
        try {
            mCameraCaptureSession.capture(request, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setTouchFocus(Rect focusArea, Rect meteringArea) {
        if (focusArea != null) {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(focusArea, IAutoFocusHelper.FOCUS_AREA_WEIGHT)});
        }
        if (meteringArea != null) {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{new MeteringRectangle(meteringArea, IAutoFocusHelper.FOCUS_AREA_WEIGHT)});
        }
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
        startRequest(mCaptureRequestBuilder);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        startCapture(mCaptureRequestBuilder);
    }

    private void checkCaptureRequest(CaptureResult result) {
        if (!isTouchFocus) {
            return;
        }
        Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
        if (afState == null) {
            //如果afState为null，默认对焦成功
            afState = CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED;
        }
        if (afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
            //对焦失败，重新对焦
            if (mFocusCount < FOCUS_COUNT_MAX) {
                startCapture(mCaptureRequestBuilder);
                mFocusCount++;
                return;
            }
        }
        if (afState != CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED && afState != CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
            return;
        }
        mAutoFocusHelper.unLock();
        isTouchFocus = false;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
        setModeToBuilder(mCaptureRequestBuilder);
        startCapture(mCaptureRequestBuilder);
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            onStartPreview(Collections.singletonList(mCameraHelperCallback.getSurfaceHelper().getSurface()));
            mLogger.i("Camera opened success");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraHelperCallback.onDisconnected(camera);
            try {
                if (mCameraDevice != null) {
                    mCameraDevice.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCameraDevice = null;
            }
            mLogger.i("Camera disconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            isReady = false;
            mCameraHelperCallback.onError(MediaManager.ERROR_OPEN_CAMERA_FAILED);
            mLogger.i("Camera opened error");
        }
    };

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCameraCaptureSession = session;
            setModeToBuilder(mCaptureRequestBuilder);
            startRequest(mCaptureRequestBuilder);
            mCameraHelperCallback.canRecorder();
            mLogger.i("Session configured success");
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            isReady = false;
            mCameraHelperCallback.onError(MediaManager.ERROR_SESSION_CREATE_FAILED);
            mLogger.i("Session configured failed");
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            mCameraHelperCallback.onCaptureStarted(mTemplateType);
            mLogger.i("Capture started." + " TemplateType : " + mTemplateType);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            mCameraHelperCallback.onCaptureProgressed(mTemplateType);
            checkCaptureRequest(partialResult);
            mLogger.i("Capture progressed." + " TemplateType : " + mTemplateType);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            mCameraHelperCallback.onCaptureCompleted(mTemplateType);
            checkCaptureRequest(result);
            isReady = true;
            mLogger.i("Capture completed." + " TemplateType : " + mTemplateType);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            isReady = false;
            mCameraHelperCallback.onError(MediaManager.ERROR_SESSION_CAPTURE_FAILED);
            mLogger.i("Capture failed." + " TemplateType : " + mTemplateType);
        }
    };

    private ImageReader.OnImageAvailableListener mImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mCameraHelperCallback.onCaptureProgressed(BaseManager.TEMPLATE_STILL_CAPTURE);
            mTemplateType = BaseManager.TEMPLATE_PREVIEW;
//            onStartPreview(Collections.singletonList(mCameraHelperCallback.getSurfaceHelper().getSurface()));
            Image image = reader.acquireNextImage();
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            byteBuffer.clear();
            image.close();
//            reader.close();
            int rotation = SupportRotationManager.getInstance().getRotation((Activity) mBaseParams.getContext(), mBaseParams.getCameraCharacteristicsInfo().getOrientation());
            String path = mBaseParams.getMediaParams().getOutputPath();
            boolean isSuccess = SupportPictureUtil.savePicture(bytes, rotation, path);
            if (isSuccess) {
                mCameraHelperCallback.onCaptureCompleted(BaseManager.TEMPLATE_STILL_CAPTURE);
            } else {
                mCameraHelperCallback.onError(BaseManager.ERROR_STILL_CAPTURE_FAILED);
            }
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
            if (!isAutoFocus) {
                mLogger.i("there is not auto focus");
                return;
            }
            mFocusCount = FOCUS_COUNT_DEFAULT;
            try {
                setTouchFocus(new Rect(0, 0, 0, 0), new Rect(0, 0, 0, 0));
            } catch (Exception e) {
                e.toString();
            }
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
            isTouchFocus = true;
            LiveSize liveSize = mCameraHelperCallback.getSurfaceHelper().getViewSize();
            Rect focusArea = null;
            if (mCharacteristicsInfo.getMaxNumFocusAreas() > 0) {
                focusArea = mAutoFocusHelper.measureAreaForFocus((Activity) mBaseParams.getContext(), x, y, 1, liveSize, mCharacteristicsInfo.getArraySize(), mCharacteristicsUse.getLensFacingType(), mCharacteristicsInfo.getOrientation());
            }
            Rect meteringArea = null;
            if (mCharacteristicsInfo.getMaxNumMeteringAreas() > 0) {
                meteringArea = mAutoFocusHelper.measureAreaForFocus((Activity) mBaseParams.getContext(), x, y, 1.5f, liveSize, mCharacteristicsInfo.getArraySize(), mCharacteristicsUse.getLensFacingType(), mCharacteristicsInfo.getOrientation());
            }
            mFocusCount = FOCUS_COUNT_DEFAULT;
            setTouchFocus(focusArea, meteringArea);
        }
    };
}
