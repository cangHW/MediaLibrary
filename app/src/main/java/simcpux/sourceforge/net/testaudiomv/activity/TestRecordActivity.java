package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

import simcpux.sourceforge.net.testaudiomv.R;
import simcpux.sourceforge.net.testaudiomv.util.AudioRecordManager;

/**
 * Created by cangHX
 * on 2018/11/14  19:04
 */
public class TestRecordActivity extends Activity {

    private AudioRecordManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test);
        mManager = AudioRecordManager.create();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mStartView:
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "XXX.pcm");
                if (file.exists()){
                    file.delete();
                }
                mManager.startRecord(file);
                break;
            case R.id.mStopView:
                mManager.stopRecord();
                break;
            case R.id.mDecoderView:
                File old = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "XXX.pcm");
                File news = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "NEWS.wav");
                mManager.decoder(old, news);
                break;
        }
    }

}
