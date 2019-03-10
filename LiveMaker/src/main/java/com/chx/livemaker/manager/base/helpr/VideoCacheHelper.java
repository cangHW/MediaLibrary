package com.chx.livemaker.manager.base.helpr;

import android.content.Context;

import com.chx.livemaker.manager.base.interfaces.IVideoCacheHelper;
import com.chx.livemaker.manager.base.params.VideoCacheWrapper;
import com.chx.livemaker.manager.thread.ThreadManager;
import com.chx.livemaker.util.FileUtil;
import com.chx.livemaker.constant.LiveMakerConstant;
import com.chx.livemaker.util.Mp4ParserUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by cangHX
 * on 2018/12/21  16:22
 */
public class VideoCacheHelper implements IVideoCacheHelper {

    public static final String CACHE = "cache_";

    private volatile VideoCacheWrapper mCacheWrapper = new VideoCacheWrapper();

    @Override
    public void appendVideo(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        String name = file.getName();
        int index = name.lastIndexOf(".");
        String suffix = name.substring(index);
        File cacheFile = new File(LiveMakerConstant.PARENT_CACHE, CACHE + System.currentTimeMillis() + suffix);
        FileUtil.createParentFile(cacheFile.getAbsolutePath());
        try {
            FileUtil.removeFile(path, cacheFile.getPath());
            mCacheWrapper.add(cacheFile.getPath());
            checkListToAppend();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    @Override
    public int getSize() {
        return mCacheWrapper.getSize();
    }

    @Override
    public void finish(MediaRecorderHelper.onDataStateChangedListener listener, String path) {
        while (true) {
            if (mCacheWrapper.hasError()) {
                listener.onDataDoFailed();
                return;
            }
            if (mCacheWrapper.getSize() == 0) {
                return;
            }
            if (mCacheWrapper.getSize() == 1) {
                moveVideoToOutput(listener, path);
                return;
            }
            checkListToAppend();
        }
    }

    @Override
    public void clear() {
        ThreadManager.getInstance().finishAllRunnable();
        mCacheWrapper.clear();
        File rootFile = new File(LiveMakerConstant.PARENT_CACHE);
        if (!rootFile.exists()) {
            return;
        }
        File file[] = rootFile.listFiles();
        for (File f : file) {
            try {
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void moveVideoToOutput(final MediaRecorderHelper.onDataStateChangedListener listener, final String outPutPath) {
        ThreadManager.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                String url = mCacheWrapper.getSingleUrl();
                try {
                    FileUtil.removeFile(url, outPutPath);
                    listener.onDataDoSuccess();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    listener.onDataDoFailed();
                }
            }
        });
    }

    private void checkListToAppend() {
        if (!mCacheWrapper.isCanUse()) {
            return;
        }
        ThreadManager.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                VideoCacheWrapper.Cache cache = mCacheWrapper.getCanUse();
                if (cache == null) {
                    return;
                }
                try {
                    Mp4ParserUtils.appendMp4List(cache.getList(), cache.updataName());
                    mCacheWrapper.updataCache(cache);
                    checkListToAppend();
                } catch (IOException e) {
                    e.printStackTrace();
                    mCacheWrapper.updataCacheError(cache);
                }
            }
        });
    }
}
