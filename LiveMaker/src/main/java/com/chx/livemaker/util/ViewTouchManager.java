package com.chx.livemaker.util;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 触摸事件监听
 * Created by cangHX
 * on 2019/01/10  19:26
 */
public class ViewTouchManager {

    public interface onTouchListener {
        void onTouch(View v, MotionEvent event);
    }

    private List<onTouchListener> mListeners = new ArrayList<>();

    private static class Factory {
        private static final ViewTouchManager mInstance = new ViewTouchManager();
    }

    public static ViewTouchManager getInstance() {
        return Factory.mInstance;
    }

    public void addTouchListener(onTouchListener listener) {
        mListeners.add(listener);
    }

    public void removeTouchListener(onTouchListener listener) {
        mListeners.remove(listener);
    }

    public void onTouch(View v, MotionEvent event) {
        for (onTouchListener listener : mListeners) {
            listener.onTouch(v, event);
        }
    }
}
