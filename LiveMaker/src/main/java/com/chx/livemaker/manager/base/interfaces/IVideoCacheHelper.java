package com.chx.livemaker.manager.base.interfaces;

import android.content.Context;

import com.chx.livemaker.manager.base.helpr.MediaRecorderHelper;

/**
 * Created by cangHX
 * on 2018/12/21  16:23
 */
public interface IVideoCacheHelper {

    void appendVideo(Context context, String path);

    int getSize();

    void finish(MediaRecorderHelper.onDataStateChangedListener listener, String path);

    void clear();

}
