package simcpux.sourceforge.net.testaudiomv.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2018/11/16  16:46
 */
public class CameraSurfaceActivity extends Activity {

    private SurfaceView mSurfaceView;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private Handler mThreadHandler;
    private TextView mTakePictureView;
    private ImageReader mImageReader;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_surface);
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(callback);
//        File file=new File(getDir("liveMaker", Context.MODE_PRIVATE), "asd.mp4");
//
        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "XXX.mp4");
//
//        try {
//            FileInputStream inputStream=new FileInputStream(file);
//            FileOutputStream outputStream=new FileOutputStream(file1);
//            byte[] bytes=new byte[1024];
//            while (inputStream.read(bytes)!=-1){
//                outputStream.write(bytes,0,bytes.length);
//            }
//            outputStream.flush();
//            inputStream.close();
//            outputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mTakePictureView = findViewById(R.id.mTakePictureView);
        mTakePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraDevice!=null){
                    try {
                        CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                        builder.addTarget(mImageReader.getSurface());
                        //自动对焦
                        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        CaptureRequest request = builder.build();
                        mSession.capture(request,null,mThreadHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mImageView=findViewById(R.id.mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraDevice!=null){
            try {
                mCameraDevice.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mCameraDevice=null;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void createCameraManager() {
        Handler handler = new Handler(getMainLooper());
        CameraManager mManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (mManager != null) {
                String[] strings = mManager.getCameraIdList();
                if (strings.length > 0) {
                    mManager.openCamera(strings[0], stateCallback, handler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview(){
        try {
            HandlerThread thread=new HandlerThread("camera");
            thread.start();
            mThreadHandler=new Handler(thread.getLooper());

            mImageReader=ImageReader.newInstance(1280,720, ImageFormat.JPEG,1);
            mImageReader.setOnImageAvailableListener(imageAvailableListener,mThreadHandler);

            mCaptureRequestBuilder= mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(mSurfaceView.getHolder().getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceView.getHolder().getSurface(),mImageReader.getSurface()),captureStateCallback,mThreadHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //surfaceview   回调
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            createCameraManager();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCameraDevice !=null){
                mCameraDevice.close();
                mCameraDevice =null;
            }
        }
    };

    //cameramanager   回调
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice =camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraDevice =null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Toast.makeText(CameraSurfaceActivity.this,"打开相机失败",Toast.LENGTH_SHORT).show();
        }
    };

    //相机预览回调
    private CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            if (mCameraDevice ==null){
                return;
            }
            mSession=session;
            try {
                //自动对焦
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 打开闪光灯
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                CaptureRequest request=mCaptureRequestBuilder.build();
                mSession.setRepeatingRequest(request,captureCallback,mThreadHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(CameraSurfaceActivity.this,"预览失败",Toast.LENGTH_SHORT).show();
        }
    };

    //拍照返回
    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes=new byte[buffer.remaining()];
            buffer.get(bytes);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            if (bitmap!=null) {
                mImageView.setImageBitmap(bitmap);
                mImageView.setVisibility(View.VISIBLE);
            }
            image.close();
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback=new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };
}
