package com.chx.livemaker.manager.base.helpr;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.chx.livemaker.manager.base.interfaces.ISurfaceHelper;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelperCallback;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.util.ViewTouchManager;

/**
 * Created by cangHX
 * on 2018/12/14  15:59
 */
@SuppressLint("ClickableViewAccessibility")
public class SurfaceViewSurfaceHelper implements ISurfaceHelper, SurfaceHolder.Callback, View.OnAttachStateChangeListener, View.OnLayoutChangeListener, View.OnTouchListener {

    private SurfaceView mSurfaceView;
    private ISurfaceHelperCallback mHelperCallback;
    private LiveSize mViewSize;

    private SurfaceViewSurfaceHelper(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }

    public static ISurfaceHelper create(SurfaceView surfaceView) {
        return new SurfaceViewSurfaceHelper(surfaceView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Size[] getSupportSizes(StreamConfigurationMap map) {
        return map.getOutputSizes(SurfaceHolder.class);
    }

    @Override
    public Surface getSurface() {
        return mSurfaceView.getHolder().getSurface();
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return null;
    }

    @Override
    public LiveSize getViewSize() {
        return mViewSize;
    }

    @Override
    public long getDelayedTime() {
        return 200;
    }

    @Override
    public void helper() {
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.addOnAttachStateChangeListener(this);
        mSurfaceView.addOnLayoutChangeListener(this);
        mSurfaceView.setOnTouchListener(this);
    }

    @Override
    public void unHelper() {
        mSurfaceView.getHolder().removeCallback(this);
        mSurfaceView.removeOnAttachStateChangeListener(this);
        mSurfaceView.removeOnLayoutChangeListener(this);
        mSurfaceView.setOnTouchListener(null);
        mHelperCallback = null;
        mSurfaceView = null;
    }

    @Override
    public void setHelperCallback(ISurfaceHelperCallback helperCallback) {
        this.mHelperCallback = helperCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHelperCallback.onSurfaceCreated(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHelperCallback.onSurfaceChanged(holder.getSurface(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHelperCallback.onSurfaceDestroyed();
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mViewSize = new LiveSize(left,top,right,bottom);
        mHelperCallback.onLayout(mViewSize.getWidth(), mViewSize.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ViewTouchManager.getInstance().onTouch(v, event);
        return false;
    }
}
