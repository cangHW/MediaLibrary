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
import android.view.TextureView;
import android.view.View;

import com.chx.livemaker.manager.base.interfaces.ISurfaceHelper;
import com.chx.livemaker.manager.base.interfaces.ISurfaceHelperCallback;
import com.chx.livemaker.manager.base.params.LiveSize;
import com.chx.livemaker.util.ViewTouchManager;

/**
 * Created by cangHX
 * on 2018/12/14  16:13
 */
@SuppressLint("ClickableViewAccessibility")
public class TextureViewSurfaceHelper implements ISurfaceHelper, TextureView.SurfaceTextureListener, View.OnLayoutChangeListener, View.OnTouchListener {

    private TextureView mTextureView;
    private LiveSize mViewSize;
    private ISurfaceHelperCallback mHelperCallback;

    private TextureViewSurfaceHelper(TextureView textureView) {
        this.mTextureView = textureView;
    }

    public static ISurfaceHelper create(TextureView textureView) {
        return new TextureViewSurfaceHelper(textureView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Size[] getSupportSizes(StreamConfigurationMap map) {
        return map.getOutputSizes(SurfaceTexture.class);
    }

    @Override
    public Surface getSurface() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
        return new Surface(surfaceTexture);
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return null;
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mTextureView.getSurfaceTexture();
    }

    @Override
    public LiveSize getViewSize() {
        return mViewSize;
    }

    @Override
    public long getDelayedTime() {
        return 0;
    }

    @Override
    public void helper() {
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOnTouchListener(this);
        mTextureView.addOnLayoutChangeListener(this);
    }

    @Override
    public void unHelper() {
        mTextureView.setOnTouchListener(null);
        mTextureView.setSurfaceTextureListener(null);
        mTextureView.removeOnLayoutChangeListener(this);
    }

    @Override
    public void setHelperCallback(ISurfaceHelperCallback helperCallback) {
        this.mHelperCallback = helperCallback;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surface.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
        mHelperCallback.onSurfaceCreated(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        surface.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
        mHelperCallback.onSurfaceChanged(new Surface(surface), width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mHelperCallback.onSurfaceDestroyed();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mViewSize = new LiveSize(left, top, right, bottom);
        mHelperCallback.onLayout(mViewSize.getWidth(), mViewSize.getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ViewTouchManager.getInstance().onTouch(v, event);
        return false;
    }
}
