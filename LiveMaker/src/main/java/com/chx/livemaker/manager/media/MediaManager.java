package com.chx.livemaker.manager.media;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.manager.base.manager.BaseManager;
import com.chx.livemaker.manager.media.recorder.RecorderImpl;
import com.chx.livemaker.manager.media.recorder.iRecorder.IRecorder;

/**
 * Created by cangHX
 * on 2018/12/14  14:47
 */
public class MediaManager extends BaseManager {

    public static IRecorder createMediaRecorder(@NonNull Activity activity) {
        return createIRecorder(activity);
    }

    public static IRecorder createMediaRecorder(@NonNull FragmentActivity activity) {
        return createIRecorder(activity);
    }

    public static IRecorder createMediaRecorder(@NonNull Fragment fragment) {
        return createIRecorder(fragment);
    }

    public static IRecorder createMediaRecorder(@NonNull android.app.Fragment fragment) {
        return createIRecorder(fragment);
    }

    private static IRecorder createIRecorder(Object object) {
        int key = getKey(object);
        Context context = addFragmentForLifecycle(object, key);
        IRecorder recorder = RecorderImpl.create(context);
        LifecycleHelper.getInstance().addLifecycleCallback(key, recorder);
        return recorder;
    }
}
