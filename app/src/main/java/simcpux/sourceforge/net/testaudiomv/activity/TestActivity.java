package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chx.livemaker.view.camera.CameraView;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2019/01/15  14:10
 */
public class TestActivity extends Activity {

    private CameraView surface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        surface=findViewById(R.id.surface);
        surface.setType(CameraView.TYPE_RECORD);
        surface.start();
    }

    public void onClick(View view){
        surface.setType(CameraView.TYPE_CAPTURE);
    }

}
