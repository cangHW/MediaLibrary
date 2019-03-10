package simcpux.sourceforge.net.testaudiomv.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import simcpux.sourceforge.net.testaudiomv.R;

/**
 * Created by cangHX
 * on 2018/11/14  18:58
 */
public class TestSurfaceActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_surface);
//        ImageView image1=findViewById(R.id.image1);
//        ImageView image2=findViewById(R.id.image2);
//        Paint paint=new Paint();
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//
//        Bitmap bitmap= BitmapFactory.decodeResource(TestSurfaceActivity.this.getResources(),R.drawable.asd);
//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getHeight(),bitmap.getWidth(),bitmap.getConfig());
//        Canvas canvas2=new Canvas(newBitmap);
//        Matrix matrix=new Matrix();
//        matrix.postTranslate(-bitmap.getWidth()/2,-bitmap.getHeight()/2);
//        matrix.postRotate(90);
//        matrix.postTranslate(bitmap.getHeight()/2,bitmap.getWidth()/2);
//        canvas2.drawBitmap(bitmap,matrix,paint);
//
//        image1.setImageBitmap(bitmap);
//        image2.setImageBitmap(newBitmap);
        SurfaceView mSfvView=findViewById(R.id.mSfvView);
        mSfvView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                if (holder==null){
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Paint paint=new Paint();
                        paint.setAntiAlias(true);
                        paint.setStyle(Paint.Style.STROKE);

                        Bitmap bitmap= BitmapFactory.decodeResource(TestSurfaceActivity.this.getResources(),R.drawable.asd);
//                        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getHeight(),bitmap.getWidth(),bitmap.getConfig());
//                        Canvas canvas2=new Canvas(newBitmap);
//                        Matrix matrix=new Matrix();
//                        matrix.postTranslate(-newBitmap.getWidth()/2,-newBitmap.getHeight()/2);
//                        matrix.postRotate(90);
//                        matrix.postTranslate(newBitmap.getWidth()/2,newBitmap.getHeight()/2);
//                        canvas2.drawBitmap(bitmap,matrix,paint);

                        Canvas canvas=holder.lockCanvas();
                        canvas.drawBitmap(bitmap,0,0,paint);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }
}
