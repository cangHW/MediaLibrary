package com.chx.livemaker.manager.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import com.chx.livemaker.callback.CameraCaptureCallback;
import com.chx.livemaker.manager.base.helpr.Camera1Helper;
import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.manager.base.helpr.SurfaceViewSurfaceHelper;
import com.chx.livemaker.manager.base.helpr.TextureViewSurfaceHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelperCallback;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelper;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelperCallback;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.camera.ICapture.ICapture;
import com.chx.livemaker.util.FileUtil;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.constant.LiveMakerConstant;
import com.chx.livemaker.util.LiveSpManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cangHX
 * on 2019/01/07  19:08
 */
public class CaptureImpl implements ICapture {

    private static final LiveLogger mLogger = LiveLogger.create(CaptureImpl.class);
    private CaptureParams mCaptureParams = new CaptureParams();

    private CaptureImpl(@NonNull Context context) {
        mCaptureParams.setContext(context);
        //设置默认文件存储地址
        this.mCaptureParams.getMediaParams().setOutputPath(new File(LiveMakerConstant.PARENT_PUBLIC, System.currentTimeMillis() + ".jpeg").getPath());
        FileUtil.createParentFile(mCaptureParams.getMediaParams().getOutputPath());
    }

    public static ICapture create(@NonNull Context context) {
        return new CaptureImpl(context);
    }

    @Override
    public ICapture setView(@NonNull SurfaceView surfaceView) {
        mCaptureParams.setSurfaceHelper(SurfaceViewSurfaceHelper.create(surfaceView));
        return this;
    }

    @Override
    public ICapture setView(@NonNull TextureView textureView) {
        mCaptureParams.setSurfaceHelper(TextureViewSurfaceHelper.create(textureView));
        return this;
    }

    @Override
    public ICapture setLifecycleEnable(boolean enable) {
        if (!enable) {
            LifecycleHelper.getInstance().removeLifecycleCallback(this);
        }
        return this;
    }

    @Override
    public ICapture setIsLevelCanDown(boolean isLevelCanDown) {
        mCaptureParams.setCanDownLevel(isLevelCanDown);
        return this;
    }

    @Override
    public ICapture setLensFacing(@NonNull String faceType) {
        mCaptureParams.setFaceType(faceType);
        return this;
    }

    @Override
    public ICapture setFilePath(@NonNull String path) {
        this.mCaptureParams.getMediaParams().setOutputPath(path);
        FileUtil.createParentFile(mCaptureParams.getMediaParams().getOutputPath());
        return this;
    }

    @Override
    public ICapture setCaptureCallback(@NonNull CameraCaptureCallback captureCallback) {
        mCaptureParams.setCameraCaptureCallback(captureCallback);
        return this;
    }

    @Override
    public void initialize() {
        if (mCaptureParams.getSurfaceHelper() == null) {
            mLogger.dOnAll("the SurfaceView or TextureView can not be empty");
            return;
        }
        if (mCaptureParams.isCanDownLevel() && LiveSpManager.isCameraLevel(mCaptureParams.getContext())) {
            mCaptureParams.setCameraHelper(new Camera1Helper());
        }

        mCaptureParams.getCameraCaptureCallback().checkParams(mCaptureParams);
        mCaptureParams.initCameraParams();

        mCaptureParams.getSurfaceHelper().setHelperCallback(mSurfaceHelperCallback);
        mCaptureParams.getCameraHelper().setHelperCallback(mCameraHelperCallback);

        mCaptureParams.getSurfaceHelper().helper();
        mLogger.i("capture initialize");
    }

    @Override
    public String getFilePath() {
        return this.mCaptureParams.getMediaParams().getOutputPath();
    }

    @Override
    public void changeLensFacing(@NonNull String faceType) {
        mCaptureParams.getCameraHelper().changeLensFacing(faceType);
    }

    @Override
    public void takeCapture() {
        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(mCaptureParams.getSurfaceHelper().getSurface());
        mCaptureParams.getCameraHelper().onStartCapture(surfaces);
    }

    @Override
    public void onLifecycleStop() {
        mCaptureParams.setCapture(false);
        mCaptureParams.stop();
    }

    @Override
    public void onLifecycleResume() {
        mCaptureParams.setCapture(false);
        mCaptureParams.resume();
    }

    @Override
    public void onLifecycleDestroy() {
        mCaptureParams.setCapture(false);
        mCaptureParams.destroyToCleanMemory();
    }

    private ISurfaceHelperCallback mSurfaceHelperCallback = new ISurfaceHelperCallback() {
        @Override
        public void onLayout(int width, int height) {

        }

        @Override
        public void onSurfaceCreated(Surface surface) {
            mCaptureParams.getCameraHelper().onCreate(mCaptureParams);
        }

        @Override
        public void onSurfaceChanged(Surface surface, int width, int height) {

        }

        @Override
        public void onSurfaceDestroyed() {

        }
    };

    private ICameraHelperCallback mCameraHelperCallback = new ICameraHelperCallback() {
        @Override
        public void downLevel() {
            LiveSpManager.setCameraLevelDown(mCaptureParams.getContext());
            if (mCaptureParams.isCanDownLevel()) {
                mCaptureParams.setCameraHelper(new Camera1Helper());
                mCaptureParams.initCameraParams();
                mCaptureParams.getCameraHelper().setHelperCallback(mCameraHelperCallback);
                mCaptureParams.getCameraHelper().onCreate(mCaptureParams);
            }
        }

        @Override
        public ISurfaceHelper getSurfaceHelper() {
            return mCaptureParams.getSurfaceHelper();
        }

        @Override
        public void checkSizeParams() {
            SizeParams sizeParams = mCaptureParams.getSizeParams();

            List<Camera.Size> previewSizeList = mCaptureParams.getCameraCharacteristicsInfo().getPreviewSizeList();
            Size[] previewSizes = mCaptureParams.getCameraCharacteristicsInfo().getPreviewSizes();
            mCaptureParams.getCameraCaptureCallback().checkPreviewSizeParams(previewSizeList, previewSizes, sizeParams);

            List<Camera.Size> pictureSizeList = mCaptureParams.getCameraCharacteristicsInfo().getPictureSizeList();
            Size[] pictureSizes = mCaptureParams.getCameraCharacteristicsInfo().getPictureSizes();
            mCaptureParams.getCameraCaptureCallback().checkPictureSizeParams(pictureSizeList, pictureSizes, sizeParams);
        }

        @Override
        public void canRecorder() {

        }

        @Override
        public void onCaptureStarted(String templateType) {

        }

        @Override
        public void onCaptureProgressed(String templateType) {

        }

        @Override
        public void onCaptureCompleted(String templateType) {

        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(int errorCode) {

        }
    };
}
