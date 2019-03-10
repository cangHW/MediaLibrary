package com.chx.livemaker.manager.base.helpr;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
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
 * camera1，对焦辅助类
 * Created by cangHX
 * on 2019/01/10  14:06
 */
public class Camera1AutoFocusHelper implements IAutoFocusHelper, SensorEventManager.onSensorEventChangedListener, ViewTouchManager.onTouchListener {

    private static final LiveLogger mLogger = LiveLogger.create(Camera1AutoFocusHelper.class);
    private static final RectF CAMERA_DRIVER_RECT = new RectF(0, 0, 2000, 2000);

    private boolean isFocus = false;
    private boolean isCanAutoFocus = true;
    private long mTime;
    private int mX;
    private int mY;
    private int mZ;

    private int mStatus = STATUS_DEFAULT;
    private IAutoFocusHelperCallback mAutoFocusCallback;

    private static class Factory {
        private static final Camera1AutoFocusHelper mInstance = new Camera1AutoFocusHelper();
    }

    public static IAutoFocusHelper getInstance() {
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
        String xx = "";
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
    public void unLock() {
        isCanAutoFocus = true;
    }

    @Override
    public void setAutoFocusCallback(IAutoFocusHelperCallback autoFocusCallback) {
        this.mAutoFocusCallback = autoFocusCallback;
    }

    @Override
    public Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, int cameraId, int orientation) {
        int areaSize = (int) (FOCUS_AREA_SIZE * scale);
        int areaX;
        int areaY;
        int rotation = SupportRotationManager.getInstance().getRotation(activity, cameraId, orientation);
        switch (rotation) {
            case 0:
                areaX = (x * FOCUS_AREA_WEIGHT * 2) / size.getWidth() - FOCUS_AREA_WEIGHT;
                areaY = (y * FOCUS_AREA_WEIGHT * 2) / size.getHeight() - FOCUS_AREA_WEIGHT;
                break;
            case 90:
                areaX = (y * FOCUS_AREA_WEIGHT * 2) / size.getHeight() - FOCUS_AREA_WEIGHT;
                areaY = FOCUS_AREA_WEIGHT - (x * FOCUS_AREA_WEIGHT * 2) / size.getWidth();
                break;
            case 180:
                areaX = FOCUS_AREA_WEIGHT - (x * FOCUS_AREA_WEIGHT * 2) / size.getWidth();
                areaY = FOCUS_AREA_WEIGHT - (y * FOCUS_AREA_WEIGHT * 2) / size.getHeight();
                break;
            case 270:
                areaX = FOCUS_AREA_WEIGHT - (y * FOCUS_AREA_WEIGHT * 2) / size.getHeight();
                areaY = (x * FOCUS_AREA_WEIGHT * 2) / size.getWidth() - FOCUS_AREA_WEIGHT;
                break;
            default:
                areaX = (x * FOCUS_AREA_WEIGHT * 2) / size.getWidth() - FOCUS_AREA_WEIGHT;
                areaY = (y * FOCUS_AREA_WEIGHT * 2) / size.getHeight() - FOCUS_AREA_WEIGHT;
                break;
        }
        int left = checkPoint(areaX - areaSize / 2);
        int top = checkPoint(areaY - areaSize / 2);
        int right = checkPoint(areaX + areaSize / 2);
        int bottom = checkPoint(areaY + areaSize / 2);
        return new Rect(left, top, right, bottom);
    }

    @Override
    public Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, Rect arraySize, int cameraId, int orientation) {
        return null;
    }

    private int checkPoint(int point) {
        if (point > FOCUS_AREA_WEIGHT) {
            return FOCUS_AREA_WEIGHT;
        }
        if (point < -FOCUS_AREA_WEIGHT) {
            return -FOCUS_AREA_WEIGHT;
        }
        return point;
    }
}
