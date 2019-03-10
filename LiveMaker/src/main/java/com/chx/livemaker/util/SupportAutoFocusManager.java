package com.chx.livemaker.util;

import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import java.util.List;

/**
 * 兼容类，检查设备是否支持对应的对焦模式
 * Created by cangHX
 * on 2019/01/10  15:22
 */
public class SupportAutoFocusManager {

    private static class Factory {
        private static final SupportAutoFocusManager mInstance = new SupportAutoFocusManager();
    }

    public static SupportAutoFocusManager getInstance() {
        return Factory.mInstance;
    }

    /******************************* camera1 ***************************************/
    public boolean isSupportPictureMode(List<String> focusModesList) {
        if (Build.MODEL.equals("KORIDY H30")) {
            //快易典h30不支持照片模式对焦
            return false;
        }
        for (String mode : focusModesList) {
            if (Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSupportVideoMode(List<String> focusModesList) {
        for (String mode : focusModesList) {
            if (Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    /******************************* camera2 ***************************************/
    public boolean isSupportPictureMode(int[] focusModes) {
        if (Build.MODEL.equals("KORIDY H30")) {
            //快易典h30不支持照片模式对焦
            return false;
        }
        for (int mode : focusModes) {
            if (CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE == mode) {
                return true;
            }
        }
        return false;
    }

    public boolean isSupportVideoMode(int[] focusModes) {
        for (int mode : focusModes) {
            if (CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO == mode) {
                return true;
            }
        }
        return false;
    }
}
