package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import simcpux.sourceforge.net.testaudiomv.R;
import simcpux.sourceforge.net.testaudiomv.util.Triangle;

/**
 * Created by cangHX
 * on 2019/01/21  10:21
 */
public class GLSurfaceViewActivity extends Activity {

    private Triangle mTriangle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_surface);
        GLSurfaceView mGlView = findViewById(R.id.mGlView);
        mGlView.setEGLContextClientVersion(2);

        mGlView.setRenderer(new GLSurfaceView.Renderer() {
            float[] mProjectionMatrix = new float[16];
            float[] mMvpMatrix = new float[16];
            float[] mViewMatrix = new float[16];

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glClearColor(0, 0, 0, 1);
                mTriangle = new Triangle();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
                float ratio = (float) width / height;
                Matrix.frustumM(mProjectionMatrix, 0, ratio, -ratio, 1, -1, 3, 7);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, -1, 0);
                Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
                mTriangle.onDraw(mMvpMatrix);

//                float[] mRotationMatrix = new float[16];
//                float[] scratch = new float[16];
//                int time = (int) (SystemClock.uptimeMillis() % 4000L);
//                float angle = time * 0.09f;
//
//                Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1f);
//                Matrix.multiplyMM(scratch, 0, mMvpMatrix, 0, mRotationMatrix, 0);
//
//                mTriangle.onDraw(scratch);
            }
        });
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
