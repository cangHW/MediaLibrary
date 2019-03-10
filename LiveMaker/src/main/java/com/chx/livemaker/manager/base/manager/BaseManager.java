package com.chx.livemaker.manager.base.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.chx.livemaker.manager.base.lifecycle.LifecycleFragmentApp;
import com.chx.livemaker.manager.base.lifecycle.LifecycleFragmentV4;
import com.chx.livemaker.constant.LiveMakerConstant;

/**
 * Created by cangHX
 * on 2019/01/08  11:13
 */
public class BaseManager {

    public static final int ERROR_OPEN_CAMERA_FAILED = 10000;
    public static final int ERROR_SESSION_CREATE_FAILED = 10001;
    public static final int ERROR_SESSION_CAPTURE_FAILED = 10002;
    public static final int ERROR_STILL_CAPTURE_FAILED = 10003;

    public static final String TEMPLATE_PREVIEW = "preview";
    public static final String TEMPLATE_RECORD = "record";
    public static final String TEMPLATE_STILL_CAPTURE = "capture";

    public static final String FACE_FRONT = "front";
    public static final String FACE_BACK = "back";

    public static final String FROM_CAMERA = "camera";
    public static final String FROM_SURFACE = "surface";
    public static final String FROM_MEDIA = "media";

    public static final String TYPE_AUDIO_RECORD = "audio_record";
    public static final String TYPE_VIDEO_RECORD = "video_record";
    public static final String TYPE_PICTURE = "picture";
    public static final String TYPE_AUDIO_PLAYER = "audio_player";
    public static final String TYPE_VIDEO_PLAYER = "video_player";

    protected static int getKey(Object object) {
        return object.hashCode();
    }

    protected static Context addFragmentForLifecycle(Object object, int key) {
        Context context = null;
        if (object instanceof FragmentActivity) {
            context = (Context) object;
            FragmentManager manager = ((FragmentActivity) object).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(LifecycleFragmentV4.create(key), LiveMakerConstant.LIFECYCLE_TAG);
            transaction.commitNowAllowingStateLoss();
        } else if (object instanceof Activity) {
            context = (Context) object;
            android.app.FragmentManager manager = ((Activity) object).getFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(LifecycleFragmentApp.create(key), LiveMakerConstant.LIFECYCLE_TAG);
            transaction.commitAllowingStateLoss();
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getContext();
            FragmentManager manager = ((Fragment) object).getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(LifecycleFragmentV4.create(key), LiveMakerConstant.LIFECYCLE_TAG);
            transaction.commitNowAllowingStateLoss();
        } else if (object instanceof android.app.Fragment) {
            context = ((android.app.Fragment) object).getActivity();
            android.app.FragmentManager manager = ((android.app.Fragment) object).getChildFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(LifecycleFragmentApp.create(key), LiveMakerConstant.LIFECYCLE_TAG);
            transaction.commitAllowingStateLoss();
        }
        return context;
    }
}
