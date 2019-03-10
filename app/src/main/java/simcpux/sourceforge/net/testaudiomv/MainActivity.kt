package simcpux.sourceforge.net.testaudiomv

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.view.View
import com.chx.livemaker.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*
import simcpux.sourceforge.net.testaudiomv.activity.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        ), 1001);
    }

    fun onClick(view: View) {
        if (view == demo) {
//            val path="/data/user/0/simcpux.sourceforge.net.testaudiomv/cache/cache_1545386984285XXX.mp4"
//            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path, "SSSSSSSS.mp4").path
//            FileUtil.removeFile(path,file);
            startActivity(Intent(this@MainActivity, DemoActivity::class.java))
        }
        if (view == demo2) {
            startActivity(Intent(this@MainActivity, Demo2Activity::class.java))
        }
        if (view == mSurfaceView) {
            startActivity(Intent(this@MainActivity, TestSurfaceActivity::class.java))
        }
        if (view == mRecordView) {
            startActivity(Intent(this@MainActivity, TestRecordActivity::class.java))
        }
        if (view == mPlayView) {
            startActivity(Intent(this@MainActivity, TestAudioTrackActivity::class.java))
        }
        if (view == mCameraSurfaceView) {
            startActivity(Intent(this@MainActivity, CameraSurfaceActivity::class.java))
        }
        if (view == mMediaRecorderSurfaceView) {
            startActivity(Intent(this@MainActivity, MediaRecorderActivity::class.java))
        }
        if (view == test) {
            startActivity(Intent(this@MainActivity, TestActivity::class.java))
        }
        if (view == mGlSurfaceView){
            startActivity(Intent(this@MainActivity,GLSurfaceViewActivity::class.java))
        }
    }
}
