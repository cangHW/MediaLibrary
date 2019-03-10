package com.chx.livemaker.manager.base.helpr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelper;
import com.chx.livemaker.manager.base.interfaces.IAutoFocusHelperCallback;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.util.LiveLogger;
import com.chx.livemaker.util.SensorEventManager;
import com.chx.livemaker.util.SupportRotationManager;
import com.chx.livemaker.util.ViewTouchManager;

/**
 * camera2，对焦辅助类
 * Created by cangHX
 * on 2019/01/14  10:26
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2AutoFocusHelper implements IAutoFocusHelper, SensorEventManager.onSensorEventChangedListener, ViewTouchManager.onTouchListener {

    private static final LiveLogger mLogger = LiveLogger.create(Camera2AutoFocusHelper.class);

    private boolean isFocus = false;
    private boolean isCanAutoFocus = true;
    private long mTime;
    private int mX;
    private int mY;
    private int mZ;

    private int mStatus = STATUS_DEFAULT;
    private IAutoFocusHelperCallback mAutoFocusCallback;

    private Camera2AutoFocusHelper() {
    }

    private static class Factory {
        private static final Camera2AutoFocusHelper mInstance = new Camera2AutoFocusHelper();
    }

    public static Camera2AutoFocusHelper getInstance() {
        return Factory.mInstance;
    }

    private void reset() {
        mTime = 0;
        mX = 0;
        mY = 0;
        mZ = 0;
        isFocus = false;
        mStatus = STATUS_DEFAULT;
    }

    @Override
    public void onStart() {
        reset();
        SensorEventManager.getInstance().addSensorEventChangedListener(this);
        ViewTouchManager.getInstance().addTouchListener(this);
        mLogger.i("AutoFocus start");
    }

    @Override
    public void onFinished() {
        reset();
        SensorEventManager.getInstance().removeSensorEventChangedListener(this);
        ViewTouchManager.getInstance().removeTouchListener(this);
        mLogger.i("AutoFocus finished");
    }

    @Override
    public void unLock() {
        isCanAutoFocus = true;
    }

    @Override
    public void setAutoFocusCallback(IAutoFocusHelperCallback autoFocusCallback) {
        this.mAutoFocusCallback = autoFocusCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isCanAutoFocus) {
            reset();
            return;
        }
        int x = (int) event.values[0];
        int y = (int) event.values[1];
        int z = (int) event.values[2];
        long time = System.currentTimeMillis();
        if (mStatus != STATUS_DEFAULT) {
            int cx = Math.abs(mX - x);
            int cy = Math.abs(mY - y);
            int cz = Math.abs(mZ - z);
            double value = Math.sqrt(cx * cx + cy * cy + cz * cz);
            if (value > MIN_MOVE_VALUE) {
                mStatus = STATUS_MOVE;
            } else {
                if (mStatus == STATUS_MOVE) {
                    mTime = time;
                    isFocus = true;
                }
                if (isFocus) {
                    if (time - mTime > DELAY_MILLIS) {
                        isFocus = false;
                        mLogger.i("start auto focus");
                        if (isCanAutoFocus) {
                            if (mAutoFocusCallback != null) {
                                mAutoFocusCallback.onAutoFocus();
                            }
                        } else {
                            reset();
                        }
                    }
                }
                mStatus = STATUS_READY;
            }
        } else {
            mStatus = STATUS_READY;
            mTime = time;
        }
        mX = x;
        mY = y;
        mZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return;
        }
        mLogger.i("motionEvent action down");
        isCanAutoFocus = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (mAutoFocusCallback != null) {
            mAutoFocusCallback.onTouchFocus(x, y);
        }
    }

    @Override
    public Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, int cameraId, int orientation) {
        return null;
    }

    @Override
    public Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, Rect arraySize, int cameraId, int orientation) {
//        int rotation = SupportRotationManager.getInstance().getRotation(activity, orientation);
        int areaSize = (int) (FOCUS_AREA_SIZE * scale);

        boolean mirrorX = cameraId == CameraCharacteristics.LENS_FACING_FRONT;
        RectF previewRect = new RectF(size.getLeft(), size.getTop(), size.getRight(), size.getBottom());
        RectF driverRect = new RectF(arraySize);
        Matrix matrix = previewToCameraTransform(mirrorX, orientation, previewRect, driverRect);

        RectF source = new RectF(Math.max(x - areaSize, 0), Math.max(y - areaSize, 0), Math.min(x + areaSize, size.getWidth()), Math.min(y + areaSize, size.getHeight()));
        RectF result = new RectF();
        matrix.mapRect(result, source);
        return new Rect((int) result.left, (int) result.top, (int) result.right, (int) result.bottom);
    }

    private Matrix previewToCameraTransform(boolean mirrorX, int sensorOrientation, RectF previewRect, RectF driverRect) {
        Matrix transform = new Matrix();
        transform.setScale(mirrorX ? -1 : 1, 1);
        transform.postRotate(-sensorOrientation);
        transform.mapRect(previewRect);
        Matrix fill = new Matrix();
        fill.setRectToRect(previewRect, driverRect, Matrix.ScaleToFit.FILL);
        transform.setConcat(fill, transform);
        return transform;
    }
}
