package com.chx.livemaker.manager.base.interfaces;

import android.view.Surface;

import com.chx.livemaker.manager.base.params.BaseParams;
import com.chx.livemaker.manager.base.params.CameraCharacteristicsInfo;
import com.chx.livemaker.manager.media.recorder.RecorderParams;

/**
 * Created by cangHX
 * on 2018/12/20  16:03
 */
public interface IMediaHelper {

    void onCreate(BaseParams baseParams, CameraCharacteristicsInfo characteristicsInfo);

    void setHelperCallback(IMediaHelperCallback mediaHelperCallback);

    Surface getSurface();

    void onStart();

    void onFinish();

    void onReset();

    void checkFile();

    void clearFiles();

    void onDestroy();

}
