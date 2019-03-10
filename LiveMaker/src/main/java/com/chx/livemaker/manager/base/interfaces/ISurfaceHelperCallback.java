package com.chx.livemaker.manager.base.interfaces;

import android.view.Surface;

/**
 * Created by cangHX
 * on 2018/12/14  19:18
 */
public interface ISurfaceHelperCallback {

    void onLayout(int width,int height);

    void onSurfaceCreated(Surface surface);

    void onSurfaceChanged(Surface surface, int width, int height);

    void onSurfaceDestroyed();
}
