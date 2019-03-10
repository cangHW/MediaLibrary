package com.chx.livemaker.manager.base.params;

/**
 * Created by cangHX
 * on 2018/12/20  16:12
 */
public class MediaParams {

    /**
     * @see android.media.MediaRecorder.AudioSource
     */
    private int audioSource;

    /**
     * @see android.media.MediaRecorder.VideoSource
     */
    private int videoSource;

    /**
     * @see android.media.MediaRecorder.OutputFormat
     */
    private int outputFormat;

    private String outputPath;

    private int videoEncodingBitRate;

    private int videoFrameRate;

    /**
     * @see android.media.MediaRecorder.VideoEncoder
     */
    private int videoEncoder;

    /**
     * @see android.media.MediaRecorder.AudioEncoder
     */
    private int audioEncoder;

    /************************************************************************************************/

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(int videoSource) {
        this.videoSource = videoSource;
    }

    public int getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getVideoEncodingBitRate() {
        return videoEncodingBitRate;
    }

    public void setVideoEncodingBitRate(int videoEncodingBitRate) {
        this.videoEncodingBitRate = videoEncodingBitRate;
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    public void setVideoFrameRate(int videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public int getVideoEncoder() {
        return videoEncoder;
    }

    public void setVideoEncoder(int videoEncoder) {
        this.videoEncoder = videoEncoder;
    }

    public int getAudioEncoder() {
        return audioEncoder;
    }

    public void setAudioEncoder(int audioEncoder) {
        this.audioEncoder = audioEncoder;
    }

}
