package com.chx.livemaker.view.camera;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.manager.base.interfaces.ILifecycleInterface;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.camera.CameraManager;
import com.chx.livemaker.manager.camera.ICapture.ICapture;
import com.chx.livemaker.manager.media.MediaManager;
import com.chx.livemaker.manager.media.recorder.iRecorder.IRecorder;

/**
 * Created by cangHX
 * on 2019/01/22  14:11
 */
public class CameraView extends FrameLayout {

    public static final String TYPE_RECORD = "record";
    public static final String TYPE_CAPTURE = "capture";

    private String mType = TYPE_CAPTURE;
    private boolean mLifecycleEnable = true;
    private boolean mIsLevelCanDown = true;
    private String mFaceType = BaseManager.FACE_BACK;
    private String mFilePath;

    private ICapture mCapture;
    private IRecorder mRecord;
    private TextureView mTextureView;
    private SurfaceView mSurfaceView;

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LifecycleHelper.getInstance().addLifecycleCallback(this.hashCode(), mLifecycleInterface);
    }

    public void setType(@NonNull String type) {
        if (mType.equals(type)) {
            return;
        }
        this.mType = type;
        if (mCapture != null) {
            mCapture.onLifecycleDestroy();
            mCapture = null;
            start();
        } else if (mRecord != null) {
            mRecord.onLifecycleDestroy();
            mRecord = null;
            start();
        }
    }

    public void setLifecycleEnable(boolean enable) {
        this.mLifecycleEnable = enable;
    }

    public void setIsLevelCanDown(boolean isLevelCanDown) {
        this.mIsLevelCanDown = isLevelCanDown;
    }

    public void setFacingBack() {
        this.mFaceType = BaseManager.FACE_BACK;
        changeFacing();
    }

    public void setFacingFront() {
        this.mFaceType = BaseManager.FACE_FRONT;
        changeFacing();
    }

    public void setFilePath(@NonNull String path) {
        this.mFilePath = path;
    }

    public void start(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mTextureView = new TextureView(getContext());
            addView(mTextureView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mSurfaceView = new SurfaceView(getContext());
            addView(mSurfaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (mType.equals(TYPE_CAPTURE)) {
            startCapture();
        } else if (mType.equals(TYPE_RECORD)) {
            startRecord();
        }
    }

    public String getFilePath() {
        if (!TextUtils.isEmpty(mFilePath)) {
            return mFilePath;
        }
        if (mRecord != null) {
            return mRecord.getFilePath();
        }
        if (mCapture != null) {
            return mCapture.getFilePath();
        }
        return "";
    }

    public ICapture getCapture() {
        return mCapture;
    }

    public IRecorder getRecord() {
        return mRecord;
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    private void changeFacing() {
        if (mRecord != null) {
            mRecord.changeLensFacing(mFaceType);
        } else if (mCapture != null) {
            mCapture.changeLensFacing(mFaceType);
        }
    }

    private void startRecord() {
        mRecord = MediaManager.createMediaRecorder((Activity) getContext());
        mRecord.setLensFacing(mFaceType).setIsLevelCanDown(mIsLevelCanDown).setLifecycleEnable(mLifecycleEnable);
        if (!TextUtils.isEmpty(mFilePath)) {
            mRecord.setFilePath(mFilePath);
        }
        if (mSurfaceView != null) {
            mRecord.setView(mSurfaceView);
        } else if (mTextureView != null) {
            mRecord.setView(mTextureView);
        }
        mRecord.initialize();
    }

    private void startCapture() {
        mCapture = CameraManager.createCameraCapture((Activity) getContext());
        mCapture.setLensFacing(mFaceType).setIsLevelCanDown(mIsLevelCanDown).setLifecycleEnable(mLifecycleEnable);
        if (!TextUtils.isEmpty(mFilePath)) {
            mCapture.setFilePath(mFilePath);
        }
        if (mSurfaceView != null) {
            mCapture.setView(mSurfaceView);
        } else if (mTextureView != null) {
            mCapture.setView(mTextureView);
        }
        mCapture.initialize();
    }

    private ILifecycleInterface mLifecycleInterface = new ILifecycleInterface() {
        @Override
        public void onLifecycleStop() {

        }

        @Override
        public void onLifecycleResume() {

        }

        @Override
        public void onLifecycleDestroy() {

        }
    };

}
