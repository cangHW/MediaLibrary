package simcpux.sourceforge.net.testaudiomv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2018/11/27  15:25
 */
public class MediaRecorderActivity extends Activity {

    private CameraDevice mCamera;
    private SurfaceView mSfvView;
    private CameraCaptureSession mSession;
    private Handler mThreadhandler;
    private CaptureRequest.Builder mBuilder;
    private CameraCharacteristics mCharacteristics;

    private Integer mSensorOrientation;

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private MediaRecorder mMediaRecorder;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediarecorder);
//        path=new File(getDir("liveMaker", Context.MODE_PRIVATE), "asd.mp4").getAbsolutePath();
        mSfvView = findViewById(R.id.mSfvView);
        mSfvView.getHolder().addCallback(holderCallback);
//        TextureView textureView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaRecorder!=null){
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mMediaRecorder=null;
            }
        }
        if (mCamera!=null){
            try {
                mCamera.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mCamera=null;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view){
//        startRecord();
        switch (view.getId()){
            case R.id.start:
                mMediaRecorder.start();
                break;
            case R.id.stop:
                mMediaRecorder.stop();
                break;
            case R.id.resume:
                mMediaRecorder.resume();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void createCameraManager() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                String[] ids = cameraManager.getCameraIdList();
                if (ids.length > 0) {
                    mCharacteristics = cameraManager.getCameraCharacteristics(ids[0]);
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    cameraManager.openCamera(ids[0], stateCallback, mainHandler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCamera(){
        try {
            initRecord();
            HandlerThread handlerThread=new HandlerThread("Preview");
            handlerThread.start();
            mThreadhandler = new Handler(handlerThread.getLooper());
            mBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mBuilder.addTarget(mMediaRecorder.getSurface());
            mBuilder.addTarget(mSfvView.getHolder().getSurface());
            mCamera.createCaptureSession(Arrays.asList(mSfvView.getHolder().getSurface(),mMediaRecorder.getSurface()),sessionStateCallback,mThreadhandler);
//            mCamera.createCaptureSession(Arrays.asList(mSfvView.getHolder().getSurface()),sessionStateCallback,mThreadhandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    private void startRecord(){
//        if (mSession!=null){
//            try {
//                mSession.stopRepeating();
//                mSession.abortCaptures();
//                mSession.close();
//                mSession=null;
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
////        initRecord();
//        try {
//            mBuilder=mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mBuilder.addTarget(mSfvView.getHolder().getSurface());
////            mBuilder.addTarget(mMediaRecorder.getSurface());
//            mCamera.createCaptureSession(Arrays.asList(mSfvView.getHolder().getSurface()),sessionStateCallback,mThreadhandler);
////            mCamera.createCaptureSession(Arrays.asList(mSfvView.getHolder().getSurface(),mMediaRecorder.getSurface()),sessionStateCallback,mThreadhandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

    private void initRecord(){
        mSensorOrientation = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "XXX.mp4");
        mMediaRecorder=new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(1440,1080);
//        mMediaRecorder.setVideoSize(mSfvView.getWidth(),mSfvView.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation){
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPreview(){
        try {
            mBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mSession.setRepeatingRequest(mBuilder.build(),captureCallback,mThreadhandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback captureCallback=new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            Log.d("qwe","captureCallback : onCaptureStarted");
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            Log.d("qwe","captureCallback : onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.d("qwe","captureCallback : onCaptureCompleted");
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d("qwe","captureCallback : onCaptureFailed");
        }
    };

    private CameraCaptureSession.StateCallback sessionStateCallback=new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d("asd","sessionStateCallback : onConfigured");
            mSession = session;
            startPreview();
//            if (mMediaRecorder!=null) {
//                mMediaRecorder.start();
//            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d("asd","sessionStateCallback : onConfigureFailed");
        }
    };

    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d("asd","stateCallback : onOpened");
            mCamera=camera;
            setCamera();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d("asd","stateCallback : onDisconnected");
            try {
                if (mCamera!=null) {
                    mCamera.close();
                    mCamera = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d("asd","stateCallback : onError");
            Toast.makeText(MediaRecorderActivity.this,"打开相机失败",Toast.LENGTH_SHORT).show();
        }
    };

    private SurfaceHolder.Callback holderCallback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("asd","holderCallback : surfaceCreated");
            createCameraManager();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d("asd","holderCallback : surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d("asd","holderCallback : surfaceDestroyed");
            try {
                if (mCamera!=null) {
                    mCamera.close();
                    mCamera = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
