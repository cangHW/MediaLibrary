package com.chx.livemaker.manager.media.recorder;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import com.chx.livemaker.R;
import com.chx.livemaker.callback.MediaRecorderCallback;
import com.chx.livemaker.manager.base.helpr.Camera1Helper;
import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.manager.base.interfaces.ICameraHelperCallback;
import com.chx.livemaker.manager.base.interfaces.IMediaHelperCallback;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelper;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelperCallback;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.media.MediaManager;
import com.chx.livemaker.manager.media.recorder.iRecorder.IRecorder;
import com.chx.livemaker.manager.base.helpr.SurfaceViewSurfaceHelper;
import com.chx.livemaker.manager.base.helpr.TextureViewSurfaceHelper;
import com.chx.livemaker.util.FileUtil;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.constant.LiveMakerConstant;
import com.chx.livemaker.util.LiveSpManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/14  14:44
 */
public class RecorderImpl implements IRecorder {

    private static final LiveLogger mLogger = LiveLogger.create(RecorderImpl.class);
    private RecorderParams mRecorderParams = new RecorderParams();

    private RecorderImpl(@NonNull Context context) {
        this.mRecorderParams.setContext(context);
        //设置默认文件存储地址
        this.mRecorderParams.getMediaParams().setOutputPath(new File(LiveMakerConstant.PARENT_PUBLIC, System.currentTimeMillis() + ".mp4").getPath());
        FileUtil.createParentFile(mRecorderParams.getMediaParams().getOutputPath());
    }

    public static RecorderImpl create(@NonNull Context context) {
        return new RecorderImpl(context);
    }

    //初始化并开始业务
    @Override
    public void initialize() {
        if (this.mRecorderParams.getSurfaceHelper() == null) {
            mLogger.dOnAll("The SurfaceView or TextureView is has ?");
            return;
        }
        //如果已经发现当前设备不支持camera2，则提前进行降级切换为camera
        if (this.mRecorderParams.isCanDownLevel() && LiveSpManager.isCameraLevel(mRecorderParams.getContext())) {
            this.mRecorderParams.setCameraHelper(new Camera1Helper());
        }
        //设置相机的部分参数
        this.mRecorderParams.initCameraParams();
        //回调接口，供开发者设置自己需要的参数或自定义部分功能
        this.mRecorderParams.getMediaRecorderCallback().checkParams(mRecorderParams);

        //为各个模块设置回调接口
        this.mRecorderParams.getCameraHelper().setHelperCallback(mCameraHelperCallback);
        this.mRecorderParams.getSurfaceHelper().setHelperCallback(mSurfaceHelperCallback);
        this.mRecorderParams.getMediaHelper().setHelperCallback(mMediaHelperCallback);

        //开始执行业务逻辑
        this.mRecorderParams.getSurfaceHelper().helper();
        mLogger.i("initialize success");
    }

    //生命周期onStop
    @Override
    public void onLifecycleStop() {
        mRecorderParams.setRecord(false);
        this.mRecorderParams.stop();
    }

    //生命周期onResume
    @Override
    public void onLifecycleResume() {
        mRecorderParams.setRecord(false);
        this.mRecorderParams.resume();
    }

    //生命周期onDestroy
    @Override
    public void onLifecycleDestroy() {
        if (mRecorderParams != null) {
            onFinishRecord();
            this.mRecorderParams.destroyToCleanMemory();
//            this.mRecorderParams = null;
        }
    }

    //获取当前文件存储位置
    @Override
    public String getFilePath() {
        return this.mRecorderParams.getMediaParams().getOutputPath();
    }

    //设置预览view : SurfaceView
    @Override
    public IRecorder setView(@NonNull SurfaceView surfaceView) {
        this.mRecorderParams.setSurfaceHelper(SurfaceViewSurfaceHelper.create(surfaceView));
        return this;
    }

    //设置预览view : TextureView
    @Override
    public IRecorder setView(@NonNull TextureView textureView) {
        this.mRecorderParams.setSurfaceHelper(TextureViewSurfaceHelper.create(textureView));
        return this;
    }

    //设置是否接管当前上下文环境生命周期,默认接管，设置false后需要手动调用对应的生命周期，如onStop等
    @Override
    public IRecorder setLifecycleEnable(boolean enable) {
        if (!enable) {
            LifecycleHelper.getInstance().removeLifecycleCallback(this);
        }
        return this;
    }

    //设置是否允许当框架发现设备对camera2支持不够时进行降级，按现有参数去生成camera
    @Override
    public IRecorder setIsLevelCanDown(boolean isLevelCanDown) {
        this.mRecorderParams.setCanDownLevel(isLevelCanDown);
        return this;
    }

    //设置默认镜头方向
    @Override
    public IRecorder setLensFacing(@NonNull String faceType) {
        this.mRecorderParams.setFaceType(faceType);
        return this;
    }

    @Override
    public IRecorder setFilePath(@NonNull String filePath) {
        this.mRecorderParams.getMediaParams().setOutputPath(filePath);
        FileUtil.createParentFile(mRecorderParams.getMediaParams().getOutputPath());
        return this;
    }

    //设置录制回调接口
    @Override
    public IRecorder setMediaRecordCallback(@NonNull MediaRecorderCallback mediaRecorderCallback) {
        this.mRecorderParams.setMediaRecorderCallback(mediaRecorderCallback);
        return this;
    }

    //改变摄像头设备(前置、后置、USB连接等三种设备，具体需要根据支持情况设置，在参数回调中可以获取所有已发现并可使用的摄像头设备)
    @Override
    public void changeLensFacing(@NonNull String faceType) {
        this.mRecorderParams.getCameraHelper().changeLensFacing(faceType);
    }

    //开始录制
    @Override
    public void onStartRecord() {
        //判断是否存在异常
        if (this.mRecorderParams.isError()) {
            mLogger.dOnAll("There is an exception to check before doing this");
            return;
        }
        //判断摄像设备是否已准备好
        if (!mRecorderParams.getCameraHelper().isReady()) {
            mLogger.dOnAll("Loading");
            return;
        }
        mRecorderParams.setRecord(true);
        mRecorderParams.getMediaHelper().onCreate(mRecorderParams, mRecorderParams.getCameraCharacteristicsInfo());
        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(mRecorderParams.getSurfaceHelper().getSurface());
        surfaces.add(mRecorderParams.getMediaHelper().getSurface());
        mRecorderParams.getCameraHelper().onStartRecord(surfaces);
    }

    //停止录制
    @Override
    public void onStopRecord() {
        //判断当前是否真正处于录制过程中，如果是则停止录制，并恢复到预览状态
        if (!mRecorderParams.isRecord()) {
            mLogger.i("should to record first");
            return;
        }
        mRecorderParams.setRecord(false);
        mRecorderParams.getMediaHelper().onFinish();
        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(mRecorderParams.getSurfaceHelper().getSurface());
        mRecorderParams.getCameraHelper().onStartPreview(surfaces);
    }

    //继续录制
    @Override
    public void onResumeRecord() {
        //判断是否存在异常
        if (this.mRecorderParams.isError()) {
            mLogger.dOnAll("There is an exception to check before doing this");
            return;
        }
        //判断摄像设备是否已准备好
        if (!mRecorderParams.getCameraHelper().isReady()) {
            mLogger.dOnAll("Loading");
            return;
        }
        mRecorderParams.setRecord(true);
        mRecorderParams.getMediaHelper().onReset();
        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(mRecorderParams.getSurfaceHelper().getSurface());
        surfaces.add(mRecorderParams.getMediaHelper().getSurface());
        mRecorderParams.getCameraHelper().onStartRecord(surfaces);
    }

    //结束录制
    @Override
    public void onFinishRecord() {
        //如果正在录制则停止录制
        if (mRecorderParams.isRecord()) {
            onStopRecord();
        }
        //处理视频文件
        mRecorderParams.getMediaHelper().checkFile();
    }

    //重置(危险操作),会删除本次已录制的视频文件
    @Override
    public void reset() {
        //如果正在录制则停止录制
        if (mRecorderParams.isRecord()) {
            onStopRecord();
        }
        //删除文件
        mRecorderParams.getMediaHelper().clearFiles();
    }

    //预览view辅助类回调
    private ISurfaceHelperCallback mSurfaceHelperCallback = new ISurfaceHelperCallback() {
        @Override
        public void onLayout(int width, int height) {

        }

        @Override
        public void onSurfaceCreated(Surface surface) {
            mRecorderParams.getCameraHelper().onCreate(mRecorderParams);
        }

        @Override
        public void onSurfaceChanged(Surface surface, int width, int height) {

        }

        @Override
        public void onSurfaceDestroyed() {
//            onLifecycleDestroy();
        }
    };

    //相机设备辅助类回调
    private ICameraHelperCallback mCameraHelperCallback = new ICameraHelperCallback() {
        @Override
        public void downLevel() {
            //不兼容camera2，需要进行降级根据现有参数调用camera展开
            LiveSpManager.setCameraLevelDown(mRecorderParams.getContext());
            if (mRecorderParams.isCanDownLevel()) {
                mRecorderParams.setCameraHelper(new Camera1Helper());
                mRecorderParams.initCameraParams();
                mRecorderParams.getCameraHelper().setHelperCallback(mCameraHelperCallback);
                mRecorderParams.getCameraHelper().onCreate(mRecorderParams);
            }
        }

        @Override
        public ISurfaceHelper getSurfaceHelper() {
            return mRecorderParams.getSurfaceHelper();
        }

        @Override
        public void checkSizeParams() {
            SizeParams sizeParams = mRecorderParams.getSizeParams();

            List<Camera.Size> previewSizeList = mRecorderParams.getCameraCharacteristicsInfo().getPreviewSizeList();
            Size[] previewSizes = mRecorderParams.getCameraCharacteristicsInfo().getPreviewSizes();
            mRecorderParams.getMediaRecorderCallback().checkPreviewSizeParams(previewSizeList, previewSizes, sizeParams);

            List<Camera.Size> videoSizeList = mRecorderParams.getCameraCharacteristicsInfo().getVideoSizeList();
            Size[] videoSizes = mRecorderParams.getCameraCharacteristicsInfo().getVideoSizes();
            mRecorderParams.getMediaRecorderCallback().checkVideoSizeParams(videoSizeList, videoSizes, sizeParams);
        }

        @Override
        public void canRecorder() {
            if (mRecorderParams.isRecord()) {
                mRecorderParams.getMediaHelper().onStart();
            }
        }

        @Override
        public void onCaptureStarted(String templateType) {
            mRecorderParams.getMediaRecorderCallback().doStart(BaseManager.FROM_CAMERA, templateType);
        }

        @Override
        public void onCaptureProgressed(String templateType) {
            mRecorderParams.getMediaRecorderCallback().doLoading(BaseManager.FROM_CAMERA, templateType, 0);
        }

        @Override
        public void onCaptureCompleted(String templateType) {
            mRecorderParams.getMediaRecorderCallback().doFinish(BaseManager.FROM_CAMERA, templateType);
            mRecorderParams.setError(false);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mRecorderParams.setError(true);
        }

        @Override
        public void onError(int errorCode) {
            mRecorderParams.setError(true);
            String errorMsg = "";
            switch (errorCode) {
                case MediaManager.ERROR_OPEN_CAMERA_FAILED:
                    errorMsg = mRecorderParams.getString(R.string.camera_open_failed);
                    break;
                case MediaManager.ERROR_SESSION_CREATE_FAILED:
                    errorMsg = mRecorderParams.getString(R.string.session_create_failed);
                    break;
                case MediaManager.ERROR_SESSION_CAPTURE_FAILED:
                    errorMsg = mRecorderParams.getString(R.string.session_capture_failed);
                    break;
            }
            mRecorderParams.getMediaRecorderCallback().onCameraError(errorCode, errorMsg);
        }
    };

    //媒体设备辅助类回调
    private IMediaHelperCallback mMediaHelperCallback = new IMediaHelperCallback() {
        @Override
        public void onMediaCreate() {

        }

        @Override
        public LiveSize getViewSize() {
            return mRecorderParams.getSurfaceHelper().getViewSize();
        }

        @Override
        public Camera getCamera() {
            return mRecorderParams.getCameraHelper().getCamera();
        }

        @Override
        public void onStart() {
            mRecorderParams.getMediaRecorderCallback().doStart(BaseManager.FROM_MEDIA, BaseManager.TEMPLATE_RECORD);
        }

        @Override
        public void onLoading(long timeMillis) {
            mRecorderParams.getMediaRecorderCallback().doLoading(BaseManager.FROM_MEDIA, BaseManager.TEMPLATE_RECORD, timeMillis);
        }

        @Override
        public void onFinish() {
            mRecorderParams.getMediaRecorderCallback().doFinish(BaseManager.FROM_MEDIA, BaseManager.TEMPLATE_RECORD);
        }

        @Override
        public void onInfo(int what, int extra) {

        }

        @Override
        public void onFileSuccess() {

        }

        @Override
        public void onFileFailed() {

        }

        @Override
        public void onError(int what, int extra, String msg) {
            mRecorderParams.getMediaRecorderCallback().onMediaError(what, extra, msg);
        }
    };
}
