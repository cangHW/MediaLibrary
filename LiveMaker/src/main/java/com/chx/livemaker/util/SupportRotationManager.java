package com.chx.livemaker.util;

import android.app.Activity;
import android.hardware.Camera;
import android.util.SparseIntArray;
import android.view.Surface;

/**
 * 兼容类，负责相机角度纠正
 * Created by cangHX
 * on 2019/01/04  15:31
 * @Function 根据不同情况计算镜头旋转角度，后面可以添加针对特殊机型的计算方法
 */
public class SupportRotationManager {

    private static final int SENSOR_ORIENTATION_DEGREES_90 = 90;
    private static final int SENSOR_ORIENTATION_DEGREES_270 = 270;

    private static final SparseIntArray ORIENTATIONS_BACK = new SparseIntArray();

    static {
        ORIENTATIONS_BACK.append(Surface.ROTATION_0, 90);
        ORIENTATIONS_BACK.append(Surface.ROTATION_90, 0);
        ORIENTATIONS_BACK.append(Surface.ROTATION_180, 270);
        ORIENTATIONS_BACK.append(Surface.ROTATION_270, 180);
    }

    private static final SparseIntArray ORIENTATIONS_FRONT = new SparseIntArray();

    static {
        ORIENTATIONS_FRONT.append(Surface.ROTATION_0, 270);
        ORIENTATIONS_FRONT.append(Surface.ROTATION_90, 180);
        ORIENTATIONS_FRONT.append(Surface.ROTATION_180, 90);
        ORIENTATIONS_FRONT.append(Surface.ROTATION_270, 0);
    }

    private static class Factory {
        private static final SupportRotationManager mInstance = new SupportRotationManager();
    }

    public static SupportRotationManager getInstance() {
        return Factory.mInstance;
    }

    //camera2
    public int getRotation(Activity activity, int orientation) {
        int result = 0;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (orientation) {
            case SENSOR_ORIENTATION_DEGREES_90:
                result = ORIENTATIONS_BACK.get(rotation);
                break;
            case SENSOR_ORIENTATION_DEGREES_270:
                result = ORIENTATIONS_FRONT.get(rotation);
                break;
        }
        return result;
    }

    //camera
    public int getRotation(Activity activity, int cameraId, int orientation) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (orientation - degrees + 360) % 360;
        }
        return result;
    }
}
