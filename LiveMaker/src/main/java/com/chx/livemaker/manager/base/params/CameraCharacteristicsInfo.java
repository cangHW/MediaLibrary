package com.chx.livemaker.manager.base.params;

import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Size;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/14  17:21
 */
public class CameraCharacteristicsInfo {

    /**
     * 相机ID
     */
    private String cameraId;
    /**
     * 摄像头类型
     */
    private int lensFacingType;
    /**
     * 设备角度
     */
    private int orientation;
    /**
     * 设备支持级别 主要针对camera2
     *
     * @see android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
     * @see android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
     * @see android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
     */
    private int supportedHardwareLevel;
    /**
     * 是否支持关闭快门声音
     */
    private boolean canDisableShutterSound;

    /**
     * 对焦区域数量
     */
    private int maxNumFocusAreas;

    /**
     * 测光区域数量
     */
    private int maxNumMeteringAreas;

    /**
     * 摄像头支持的最大分辨率，供camera2对焦使用
     */
    private Rect arraySize;

    /**
     * 闪光灯
     */
    private boolean flashAvailable = false;
    private List<String> flashModeList;
    private int[] flashModes;

    /**
     * 对焦
     */
    private boolean focusAvailable = false;
    private List<String> focusModesList;
    private int[] focusModes;

    /**
     * 场景
     */
    private boolean sceneAvailable = false;
    private List<String> sceneModeList;
    private int[] sceneModes;

    /**
     * 色彩校正
     */
    private boolean colorEffectsAvailable = false;
    private List<String> colorEffectList;
    private int[] effectModes;

    /**
     * 缩略图尺寸
     */
    private boolean jpegThumbnailSizesAvailable = false;
    private List<Camera.Size> jpegThumbnailSizeList;
    private Size[] jpegThumbnailSizes;

    /**
     * 图片尺寸
     */
    private boolean pictureSizesAvailable = false;
    private List<Camera.Size> pictureSizeList;
    private Size[] pictureSizes;

    /**
     * 预览尺寸
     */
    private boolean previewSizesAvailable = false;
    private List<Camera.Size> previewSizeList;
    private Size[] previewSizes;

    /**
     * 视频尺寸
     */
    private boolean videoSizesAvailable = false;
    private List<Camera.Size> videoSizeList;
    private Size[] videoSizes;

    /**
     * 视频稳定模式
     */
    private boolean videoStabilizationAvailable = false;
    private int[] videoStabilizationModes;

    /**
     * 白平衡模式
     */
    private int[] awbModes;

    /****************************************************************/

    public Rect getArraySize() {
        return arraySize;
    }

    public void setArraySize(Rect arraySize) {
        this.arraySize = arraySize;
    }

    public int getMaxNumFocusAreas() {
        return maxNumFocusAreas;
    }

    public void setMaxNumFocusAreas(int maxNumFocusAreas) {
        this.maxNumFocusAreas = maxNumFocusAreas;
    }

    public int getMaxNumMeteringAreas() {
        return maxNumMeteringAreas;
    }

    public void setMaxNumMeteringAreas(int maxNumMeteringAreas) {
        this.maxNumMeteringAreas = maxNumMeteringAreas;
    }

    public boolean isVideoStabilizationAvailable() {
        return videoStabilizationAvailable;
    }

    public void setVideoStabilizationAvailable(boolean videoStabilizationAvailable) {
        this.videoStabilizationAvailable = videoStabilizationAvailable;
    }

    public Size[] getJpegThumbnailSizes() {
        return jpegThumbnailSizes;
    }

    public void setJpegThumbnailSizes(Size[] jpegThumbnailSizes) {
        this.jpegThumbnailSizes = jpegThumbnailSizes;
    }

    public Size[] getPictureSizes() {
        return pictureSizes;
    }

    public void setPictureSizes(Size[] pictureSizes) {
        this.pictureSizes = pictureSizes;
    }

    public Size[] getPreviewSizes() {
        return previewSizes;
    }

    public void setPreviewSizes(Size[] previewSizes) {
        this.previewSizes = previewSizes;
    }

    public List<String> getFlashModeList() {
        return flashModeList;
    }

    public void setFlashModeList(List<String> flashModeList) {
        this.flashModeList = flashModeList;
    }

    public List<String> getFocusModesList() {
        return focusModesList;
    }

    public void setFocusModesList(List<String> focusModesList) {
        this.focusModesList = focusModesList;
    }

    public List<String> getSceneModeList() {
        return sceneModeList;
    }

    public void setSceneModeList(List<String> sceneModeList) {
        this.sceneModeList = sceneModeList;
    }

    public boolean isColorEffectsAvailable() {
        return colorEffectsAvailable;
    }

    public void setColorEffectsAvailable(boolean colorEffectsAvailable) {
        this.colorEffectsAvailable = colorEffectsAvailable;
    }

    public List<String> getColorEffectList() {
        return colorEffectList;
    }

    public void setColorEffectList(List<String> colorEffectList) {
        this.colorEffectList = colorEffectList;
    }

    public boolean isJpegThumbnailSizesAvailable() {
        return jpegThumbnailSizesAvailable;
    }

    public void setJpegThumbnailSizesAvailable(boolean jpegThumbnailSizesAvailable) {
        this.jpegThumbnailSizesAvailable = jpegThumbnailSizesAvailable;
    }

    public List<Camera.Size> getJpegThumbnailSizeList() {
        return jpegThumbnailSizeList;
    }

    public void setJpegThumbnailSizeList(List<Camera.Size> jpegThumbnailSizeList) {
        this.jpegThumbnailSizeList = jpegThumbnailSizeList;
    }

    public boolean isPictureSizesAvailable() {
        return pictureSizesAvailable;
    }

    public void setPictureSizesAvailable(boolean pictureSizesAvailable) {
        this.pictureSizesAvailable = pictureSizesAvailable;
    }

    public List<Camera.Size> getPictureSizeList() {
        return pictureSizeList;
    }

    public void setPictureSizeList(List<Camera.Size> pictureSizeList) {
        this.pictureSizeList = pictureSizeList;
    }

    public boolean isPreviewSizesAvailable() {
        return previewSizesAvailable;
    }

    public void setPreviewSizesAvailable(boolean previewSizesAvailable) {
        this.previewSizesAvailable = previewSizesAvailable;
    }

    public List<Camera.Size> getPreviewSizeList() {
        return previewSizeList;
    }

    public void setPreviewSizeList(List<Camera.Size> previewSizeList) {
        this.previewSizeList = previewSizeList;
    }

    public boolean isVideoSizesAvailable() {
        return videoSizesAvailable;
    }

    public void setVideoSizesAvailable(boolean videoSizesAvailable) {
        this.videoSizesAvailable = videoSizesAvailable;
    }

    public List<Camera.Size> getVideoSizeList() {
        return videoSizeList;
    }

    public void setVideoSizeList(List<Camera.Size> videoSizeList) {
        this.videoSizeList = videoSizeList;
    }

    public boolean isCanDisableShutterSound() {
        return canDisableShutterSound;
    }

    public void setCanDisableShutterSound(boolean canDisableShutterSound) {
        this.canDisableShutterSound = canDisableShutterSound;
    }

    public int[] getAwbModes() {
        return awbModes;
    }

    public void setAwbModes(int[] awbModes) {
        this.awbModes = awbModes;
    }

    public int[] getVideoStabilizationModes() {
        return videoStabilizationModes;
    }

    public void setVideoStabilizationModes(int[] videoStabilizationModes) {
        this.videoStabilizationModes = videoStabilizationModes;
    }

    public int[] getEffectModes() {
        return effectModes;
    }

    public void setEffectModes(int[] effectModes) {
        this.effectModes = effectModes;
    }

    public int getSupportedHardwareLevel() {
        return supportedHardwareLevel;
    }

    public void setSupportedHardwareLevel(int supportedHardwareLevel) {
        this.supportedHardwareLevel = supportedHardwareLevel;
    }

    public int[] getFlashModes() {
        return flashModes;
    }

    public void setFlashModes(int[] flashModes) {
        this.flashModes = flashModes;
    }

    public boolean isFocusAvailable() {
        return focusAvailable;
    }

    public void setFocusAvailable(boolean focusAvailable) {
        this.focusAvailable = focusAvailable;
    }

    public int[] getFocusModes() {
        return focusModes;
    }

    public void setFocusModes(int[] focusModes) {
        this.focusModes = focusModes;
    }

    public boolean isSceneAvailable() {
        return sceneAvailable;
    }

    public void setSceneAvailable(boolean sceneAvailable) {
        this.sceneAvailable = sceneAvailable;
    }

    public int[] getSceneModes() {
        return sceneModes;
    }

    public void setSceneModes(int[] sceneModes) {
        this.sceneModes = sceneModes;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public int getLensFacingType() {
        return lensFacingType;
    }

    public void setLensFacingType(int lensFacingType) {
        this.lensFacingType = lensFacingType;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isFlashAvailable() {
        return flashAvailable;
    }

    public void setFlashAvailable(boolean flashAvailable) {
        this.flashAvailable = flashAvailable;
    }

    public Size[] getVideoSizes() {
        return videoSizes;
    }

    public void setVideoSizes(Size[] videoSizes) {
        this.videoSizes = videoSizes;
    }
}
