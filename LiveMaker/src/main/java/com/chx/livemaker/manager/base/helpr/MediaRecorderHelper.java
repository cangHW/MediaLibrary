package com.chx.livemaker.manager.base.helpr;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.Surface;

import com.chx.livemaker.R;
import com.chx.livemaker.manager.base.interfaces.IMediaHelper;
import com.chx.livemaker.manager.base.interfaces.IMediaHelperCallback;
import com.chx.livemaker.manager.base.interfaces.IVideoCacheHelper;
import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsInfo;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.manager.base.params.MediaParams;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.util.FileUtil;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.util.SupportSizeManager;
import com.chx.livemaker.util.SupportRotationManager;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/20  16:00
 */
public class MediaRecorderHelper implements IMediaHelper {

    public interface onDataStateChangedListener {
        void onDataDoSuccess();

        void onDataDoFailed();
    }

    private static final long DELAY_MILLIS = 1000;
    private static final LiveLogger mLogger = LiveLogger.create(MediaRecorderHelper.class);
    private static final IVideoCacheHelper mVideoCacheHelper = new VideoCacheHelper();
    private static final RecordTimeHandle mTimeHandle = new RecordTimeHandle();

    private Context mContext;
    private MediaParams mMediaParams;
    private LiveSize mVideoSize;
    private CameraCharacteristicsInfo mCharacteristicsInfo;
    private MediaRecorder mMediaRecorder;
    private IMediaHelperCallback mMediaHelperCallback;

    private boolean isStart = false;

    @Override
    public void onCreate(BaseParams baseParams, CameraCharacteristicsInfo characteristicsInfo) {
        mLogger.i("media recorder is create");
        mTimeHandle.setContext(baseParams.getContext());
        mContext = baseParams.getContext();
        mMediaParams = baseParams.getMediaParams();
        mCharacteristicsInfo = characteristicsInfo;
        File file = new File(mMediaParams.getOutputPath());
        if (file.exists()) {
            file.delete();
        }
        FileUtil.createParentFile(mMediaParams.getOutputPath());
        checkVideoSize(baseParams, characteristicsInfo.getVideoSizes(), characteristicsInfo.getVideoSizeList());
        mVideoSize = baseParams.getSizeParams().getVideoSize();
        mMediaRecorder = new MediaRecorder();
        setMediaToPrepare();
        mMediaHelperCallback.onMediaCreate();
    }

    @Override
    public void setHelperCallback(IMediaHelperCallback mediaHelperCallback) {
        this.mMediaHelperCallback = mediaHelperCallback;
        mTimeHandle.setIMediaHelperCallback(mediaHelperCallback);
    }

    @Override
    public Surface getSurface() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mMediaRecorder.getSurface();
            }
        } catch (Exception e) {
            e.toString();
        }
        return null;
    }

    @Override
    public void onStart() {
        if (isStart) {
            return;
        }
        isStart = true;
        mMediaRecorder.start();
        mTimeHandle.start();
        mMediaHelperCallback.onStart();
        mLogger.i("media record is start");
    }

    @Override
    public void onFinish() {
        setMediaFinish();
        int size = mVideoCacheHelper.getSize();
        if (size > 0) {
            mVideoCacheHelper.appendVideo(mContext, mMediaParams.getOutputPath());
        }
        mLogger.i("media record is finish");
    }

    @Override
    public void onReset() {
        if (mMediaRecorder != null) {
            onFinish();
            mVideoCacheHelper.appendVideo(mContext, mMediaParams.getOutputPath());
        } else {
            mMediaRecorder = new MediaRecorder();
        }
        setMediaToPrepare();
        mLogger.i("media record is reset");
    }

    @Override
    public void checkFile() {
        if (mDataStateChangedListener == null || mMediaParams == null) {
            return;
        }
        mVideoCacheHelper.finish(mDataStateChangedListener, mMediaParams.getOutputPath());
        mLogger.i("output url is start create");
    }

    @Override
    public void clearFiles() {
        mVideoCacheHelper.clear();
        mLogger.i("all files is delete");
    }

    @Override
    public void onDestroy() {
        if (mMediaRecorder != null) {
            setMediaFinish();
            try {
                mMediaRecorder.release();
            } catch (Exception e) {
                e.toString();
            } finally {
                mMediaRecorder = null;
            }
        }
        mLogger.i("media record is destroy");
    }

    private void setMediaFinish() {
        if (isStart) {
            isStart = false;
            mTimeHandle.finish();
            mMediaHelperCallback.onFinish();
            if (mMediaRecorder != null) {
                try {
                    mMediaRecorder.stop();
                } catch (Exception e) {
                    e.toString();
                }
                try {
                    mMediaRecorder.reset();
                } catch (Exception e) {
                    e.toString();
                }
            }
        }
    }

    private void setMediaToPrepare() {
        Camera camera = mMediaHelperCallback.getCamera();
        if (camera != null) {
            //不能锁定camera
            camera.unlock();
            mMediaRecorder.setCamera(camera);
        }
        mMediaRecorder.setAudioSource(mMediaParams.getAudioSource());
        mMediaRecorder.setVideoSource(mMediaParams.getVideoSource());
        mMediaRecorder.setOutputFormat(mMediaParams.getOutputFormat());
        mMediaRecorder.setOutputFile(mMediaParams.getOutputPath());
        mMediaRecorder.setVideoEncodingBitRate(mMediaParams.getVideoEncodingBitRate());
        mMediaRecorder.setVideoFrameRate(mMediaParams.getVideoFrameRate());
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(mMediaParams.getVideoEncoder());
        mMediaRecorder.setAudioEncoder(mMediaParams.getAudioEncoder());
        mMediaRecorder.setOnErrorListener(mErrorListener);
        mMediaRecorder.setOnInfoListener(mInfoListener);
        mMediaRecorder.setOrientationHint(SupportRotationManager.getInstance().getRotation((Activity) mContext, mCharacteristicsInfo.getOrientation()));
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLogger.i("media record is prepare");
    }

    private void checkVideoSize(BaseParams baseParams, Size[] sizes, List<Camera.Size> sizeList) {
        SizeParams sizeParams = baseParams.getSizeParams();
        if (sizeParams.getVideoSize() != null) {
            return;
        }
        LiveSize size = SupportSizeManager.getInstance().getVideoSize(mMediaHelperCallback.getViewSize(), sizes, sizeList);
        sizeParams.setVideoSize(size);
    }

    private MediaRecorder.OnInfoListener mInfoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            mMediaHelperCallback.onInfo(what, extra);
            mLogger.i("media record Info." + " Mr : " + mr + " What : " + what + " Extra : " + extra);
        }
    };

    private MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            mMediaHelperCallback.onError(what, extra, "");
            mLogger.i("media record is Error." + " Mr : " + mr + " What : " + what + " Extra : " + extra);
        }
    };

    private onDataStateChangedListener mDataStateChangedListener = new onDataStateChangedListener() {
        @Override
        public void onDataDoSuccess() {
            mMediaHelperCallback.onFileSuccess();
            mLogger.i("output url is create success");
        }

        @Override
        public void onDataDoFailed() {
            mMediaHelperCallback.onFileFailed();
            mLogger.i("output url is create failed");
        }
    };

    private static class RecordTimeHandle extends Handler {

        private WeakReference<IMediaHelperCallback> mediaHelperCallbackWeakReference;
        private WeakReference<Context> contextWeakReference;
        private long mStartTime = 0;

        RecordTimeHandle() {
        }

        void setIMediaHelperCallback(IMediaHelperCallback mediaHelperCallback) {
            mediaHelperCallbackWeakReference = new WeakReference<>(mediaHelperCallback);
        }

        void setContext(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        void start() {
            mTimeHandle.sendEmptyMessageDelayed(0, DELAY_MILLIS);
            mStartTime = System.currentTimeMillis();
        }

        void finish() {
            mTimeHandle.removeCallbacksAndMessages(null);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTimeHandle.sendEmptyMessageDelayed(0, DELAY_MILLIS);
            long time = System.currentTimeMillis();
            if (mediaHelperCallbackWeakReference != null) {
                final IMediaHelperCallback callback = mediaHelperCallbackWeakReference.get();
                if (callback != null) {
                    if (mStartTime == 0) {
                        if (contextWeakReference != null) {
                            final Context context = contextWeakReference.get();
                            if (context != null) {
                                callback.onError(0, 0, context.getString(R.string.create_record_time_error));
                            }
                        }
                    } else {
                        callback.onLoading(time - mStartTime);
                    }
                }
            }
        }
    }
}
