package com.chx.livemaker.constant;

import android.os.Environment;

/**
 * Created by cangHX
 * on 2018/12/24  18:18
 */
public class LiveMakerConstant {

    private static final String ROOT_DIR_PUBLIC = "/liveMaker";
    private static final String ROOT_DIR_CACHE = "/.liveMakerCache";

    //默认文件路径
    public static final String PARENT_PUBLIC = Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIR_PUBLIC;
    //缓存文件路径，主要用于音视频合成等
    public static final String PARENT_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIR_CACHE;


    //生命周期传参需要的key
    public static final String LIFECYCLE_TAG = "lifecycle";
}
