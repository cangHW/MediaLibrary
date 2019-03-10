package com.chx.livemaker.manager.base.params;

/**
 * Created by cangHX
 * on 2019/01/09  14:05
 */
public class SizeParams {

    private LiveSize previewSize;

    private LiveSize pictureSize;

    private LiveSize videoSize;

    public LiveSize getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(LiveSize previewSize) {
        this.previewSize = previewSize;
    }

    public LiveSize getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(LiveSize pictureSize) {
        this.pictureSize = pictureSize;
    }

    public LiveSize getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(LiveSize videoSize) {
        this.videoSize = videoSize;
    }
}
