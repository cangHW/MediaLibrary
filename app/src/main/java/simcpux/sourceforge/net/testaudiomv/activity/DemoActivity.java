package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.chx.livemaker.callback.MediaRecorderCallback;
import com.chx.livemaker.manager.base.params.SizeParams;
import com.chx.livemaker.manager.media.MediaManager;
import com.chx.livemaker.manager.media.recorder.RecorderParams;
import com.chx.livemaker.manager.media.recorder.iRecorder.IRecorder;

import java.util.List;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2018/12/17  17:18
 */
public class DemoActivity extends Activity {

    private IRecorder record;

    private String faceType = MediaManager.FACE_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        TextureView mSfvView = findViewById(R.id.mSfvView);
        record = MediaManager.createMediaRecorder(this);
        record.setView(mSfvView).setIsLevelCanDown(true).setLensFacing(faceType).setLifecycleEnable(true).setMediaRecordCallback(recorderCallback).initialize();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mChang:
                if (faceType.equals(MediaManager.FACE_FRONT)) {
                    faceType = MediaManager.FACE_BACK;
                } else {
                    faceType = MediaManager.FACE_FRONT;
                }
                record.changeLensFacing(faceType);
                break;
            case R.id.mStart:
                record.onStartRecord();
                break;
            case R.id.mStop:
                record.onStopRecord();
                break;
            case R.id.mResume:
                record.onResumeRecord();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        record.onLifecycleDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        record.onLifecycleStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        record.onLifecycleResume();
    }

    private MediaRecorderCallback recorderCallback = new MediaRecorderCallback() {
        @Override
        public void checkParams(RecorderParams params) {
//            params.setCameraHelper(new Camera1Helper());
//            params.getCameraCharacteristicsUse().setLensFacingType(facing);
        }

        @Override
        public void checkPreviewSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {
            String xx="";
        }

        @Override
        public void checkVideoSizeParams(List<Camera.Size> sizeList, Size[] sizes, SizeParams sizeParams) {
            String xx="";
        }

        @Override
        public void doStart(String from, String type) {

        }

        @Override
        public void doLoading(String from, String type, long progress) {

        }

        @Override
        public void doFinish(String from, String type) {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onCameraError(int errorCode, String errorMsg) {

        }

        @Override
        public void onMediaError(int what, int extra, String errorMsg) {

        }
    };
}
