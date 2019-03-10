package com.chx.livemaker.manager.base.interfaces;

import android.app.Activity;
import android.graphics.Rect;

import com.chx.livemaker.manager.base.params.LiveSize;

/**
 * Created by cangHX
 * on 2019/01/10  14:07
 */
public interface IAutoFocusHelper {
    double MIN_MOVE_VALUE = 1.4;
    int DELAY_MILLIS = 500;
    int FOCUS_AREA_SIZE = 50;
    int FOCUS_AREA_WEIGHT = 1000;

    int STATUS_DEFAULT = 0x000;
    int STATUS_READY = 0x001;
    int STATUS_MOVE = 0x002;

    void onStart();

    void onFinished();

    void unLock();

    void setAutoFocusCallback(IAutoFocusHelperCallback autoFocusCallback);

    Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, int cameraId, int orientation);

    Rect measureAreaForFocus(Activity activity, int x, int y, float scale, LiveSize size, Rect arraySize, int cameraId, int orientation);
}
