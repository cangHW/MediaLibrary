package com.chx.livemaker.util;

import android.hardware.Camera;
import android.os.Build;
import android.util.Size;

import com.chx.livemaker.manager.base.params.LiveSize;

import java.util.List;

/**
 * 兼容类，负责找出最合适的分辨率
 * Created by cangHX
 * on 2019/01/08  14:17
 */
public class SupportSizeManager {

    private static class Factory {
        private static final SupportSizeManager mInstance = new SupportSizeManager();
    }

    public static SupportSizeManager getInstance() {
        return Factory.mInstance;
    }

    //TODO 分辨率优化
    public LiveSize getVideoSize(LiveSize viewSize, Size[] sizes, List<Camera.Size> sizeList) {
        int w = 0;
        int h = 0;
        float per = viewSize.getHeight() * 1.0f / viewSize.getWidth();
        float gap = 100;
        int count;
        if (sizes == null) {
            count = sizeList.size();
        } else {
            count = sizes.length;
        }
        for (int i = 0; i < count; i++) {
            int width;
            int height;
            if (sizes == null) {
                width = sizeList.get(i).width;
                height = sizeList.get(i).height;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    width = sizes[i].getWidth();
                    height = sizes[i].getHeight();
                } else {
                    width = 0;
                    height = 0;
                }
            }
            float p = width * 1.0f / height;
            if (Math.abs(p - per) < gap) {
                gap = Math.abs(p - per);
                w = width;
                h = height;
            }
        }
        return new LiveSize(w, h);
    }

    //TODO 分辨率优化
    public LiveSize getPictureSize(LiveSize viewSize, Size[] sizes, List<Camera.Size> sizeList) {
        return getVideoSize(viewSize, sizes, sizeList);
    }

    //TODO 分辨率优化
    public LiveSize getPreViewSize(LiveSize viewSize, Size[] sizes, List<Camera.Size> sizeList) {
        return getVideoSize(viewSize, sizes, sizeList);
    }
}
