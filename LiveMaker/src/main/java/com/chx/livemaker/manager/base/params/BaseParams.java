package com.chx.livemaker.manager.base.params;

import android.content.Context;

import com.chx.livemaker.manager.base.interfaces.IParamsInterface;

import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/14  16:37
 */
public abstract class BaseParams implements IParamsInterface {

    private Context context;

    private boolean isCanDownLevel = true;

    private String type;

    /**
     * 用户选择的相机设置
     */
    private CameraCharacteristicsUse characteristicsUse;
    /**
     * 设备支持的相机设置
     */
    private List<CameraCharacteristicsInfo> characteristicsInfos;

    /**
     * 媒体相关属性
     */
    private MediaParams mediaParams;

    /**
     * 尺寸相关属性
     * */
    private SizeParams sizeParams;

    /**************************************************************************************/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SizeParams getSizeParams() {
        return sizeParams;
    }

    public void setSizeParams(SizeParams sizeParams) {
        this.sizeParams = sizeParams;
    }

    public boolean isCanDownLevel() {
        return isCanDownLevel;
    }

    public void setCanDownLevel(boolean canDownLevel) {
        isCanDownLevel = canDownLevel;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CameraCharacteristicsUse getCameraCharacteristicsUse() {
        return characteristicsUse;
    }

    public void setCameraCharacteristicsUse(CameraCharacteristicsUse characteristicsUse) {
        this.characteristicsUse = characteristicsUse;
    }

    public void setCharacteristicsInfos(List<CameraCharacteristicsInfo> characteristicsInfos) {
        this.characteristicsInfos = characteristicsInfos;
    }

    public void addCameraCharacteristicsInfo(CameraCharacteristicsInfo info) {
        characteristicsInfos.add(info);
    }

    public List<CameraCharacteristicsInfo> getCharacteristicsInfo() {
        return characteristicsInfos;
    }

    public MediaParams getMediaParams() {
        return mediaParams;
    }

    public void setMediaParams(MediaParams mediaParams) {
        this.mediaParams = mediaParams;
    }

    public CameraCharacteristicsInfo getCameraCharacteristicsInfo() {
        CameraCharacteristicsUse characteristicsUse = getCameraCharacteristicsUse();
        for (CameraCharacteristicsInfo params : getCharacteristicsInfo()) {
            if (params.getLensFacingType() == characteristicsUse.getLensFacingType()) {
                return params;
            }
        }
        return null;
    }
}
