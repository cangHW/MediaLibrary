package com.chx.livemaker.manager.base.params;

/**
 * 坐标辅助类
 * Created by cangHX
 * on 2019/01/08  14:12
 */
public class LiveSize {

    private final int mWidth;
    private final int mHeight;

    private final int mLeft;
    private final int mTop;
    private final int mRight;
    private final int mBottom;

    public LiveSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mLeft = 0;
        mTop = 0;
        mRight = width;
        mBottom = height;
    }

    public LiveSize(int left, int top, int right, int bottom) {
        mWidth = right - left;
        mHeight = bottom - top;
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getRight() {
        return mRight;
    }

    public int getBottom() {
        return mBottom;
    }
}
