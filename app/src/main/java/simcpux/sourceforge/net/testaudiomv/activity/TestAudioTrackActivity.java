package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

import simcpux.sourceforge.net.testaudiomv.R;
import simcpux.sourceforge.net.testaudiomv.util.AudioPlayManager;

/**
 * Created by cangHX
 * on 2018/11/15  17:07
 */
public class TestAudioTrackActivity extends Activity {

    private AudioPlayManager mManager=AudioPlayManager.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testaudiotrack);
    }

    public void onClick(View view){
        if (view.getId()==R.id.mPlayView){
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "NEWS.wav");
            mManager.playStream(file);
        }
        if (view.getId()==R.id.mPlay1View){
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), "NEWS.wav");
            mManager.playStatic(file);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.destory();
    }
}
