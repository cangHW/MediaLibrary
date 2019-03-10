package com.chx.livemaker.manager.camera;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.camera.ICapture.ICapture;

/**
 * Created by cangHX
 * on 2019/01/07  19:05
 */
public class CameraManager extends BaseManager {

    public static ICapture createCameraCapture(@NonNull Activity activity) {
        return createICapture(activity);
    }

    public static ICapture createCameraCapture(@NonNull FragmentActivity activity) {
        return createICapture(activity);
    }

    public static ICapture createCameraCapture(@NonNull Fragment fragment) {
        return createICapture(fragment);
    }

    public static ICapture createCameraCapture(@NonNull android.app.Fragment fragment) {
        return createICapture(fragment);
    }

    private static ICapture createICapture(Object object) {
        int key = getKey(object);
        Context context = addFragmentForLifecycle(object, key);
        ICapture recorder = CaptureImpl.create(context);
        LifecycleHelper.getInstance().addLifecycleCallback(key, recorder);
        return recorder;
    }

}
