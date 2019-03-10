package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.SurfaceView;
import android.view.View;

import com.chx.livemaker.callback.CameraCaptureCallback;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.camera.CameraManager;
import com.chx.livemaker.manager.camera.CaptureParams;
import com.chx.livemaker.manager.camera.ICapture.ICapture;

import java.util.List;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2018/12/27  14:19
 */
public class Demo2Activity extends Activity {

    private ICapture mCapture;
    private String face;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        SurfaceView mSurView=findViewById(R.id.mSurView);
        face=BaseManager.FACE_BACK;
        mCapture=CameraManager.createCameraCapture(this);
        mCapture.setView(mSurView).setIsLevelCanDown(true).setLensFacing(face).setCaptureCallback(captureCallback).initialize();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.picture:
                mCapture.takeCapture();
                break;
            case R.id.change:
                if (face.equals(BaseManager.FACE_BACK)){
                    face=BaseManager.FACE_FRONT;
                }else {
                    face=BaseManager.FACE_BACK;
                }
                mCapture.changeLensFacing(face);
                break;
        }
    }

    private CameraCaptureCallback captureCallback=new CameraCaptureCallback() {
        @Override
        public void checkParams(CaptureParams params) {

        }

        @Override
        public void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {
            String xx="";
        }

        @Override
        public void checkPictureSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {
            String xx="";
        }

        @Override
        public void doStart() {

        }

        @Override
        public void doLoading() {

        }

        @Override
        public void doFinish() {

        }

        @Override
        public void onError(int errorCode, String errorMsg) {

        }
    };
}
