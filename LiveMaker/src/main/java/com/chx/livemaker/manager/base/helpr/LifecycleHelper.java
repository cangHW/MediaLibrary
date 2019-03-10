package com.chx.livemaker.manager.base.helpr;

import android.content.Context;
import android.util.SparseArray;

import com.chx.livemaker.manager.base.interfaces.ILifecycleInterface;
import com.chx.livemaker.util.SensorEventManager;

/**
 * Created by cangHX
 * on 2018/12/26  14:25
 */
public class LifecycleHelper {

    private SparseArray<ILifecycleInterface> mSparseArray = new SparseArray<>();

    private LifecycleHelper() {
    }

    private static class Factory {
        private static final LifecycleHelper mInstance = new LifecycleHelper();
    }

    public static LifecycleHelper getInstance() {
        return Factory.mInstance;
    }

    public void addLifecycleCallback(int key, ILifecycleInterface iLifecycle) {
        mSparseArray.put(key, iLifecycle);
    }

    public void removeLifecycleCallback(ILifecycleInterface iLifecycle) {
        try {
            int index = mSparseArray.indexOfValue(iLifecycle);
            mSparseArray.removeAt(index);
        } catch (Exception e) {
            e.toString();
        }
    }

    public void onCreate(Context context, int key){
        SensorEventManager.getInstance().register(context);
    }

    public void onStop(Context context, int key) {
        mSparseArray.get(key, mLifecycleEmptyImpl).onLifecycleStop();
        SensorEventManager.getInstance().unRegister();
    }

    public void onResume(Context context, int key) {
        mSparseArray.get(key, mLifecycleEmptyImpl).onLifecycleResume();
        SensorEventManager.getInstance().register(context);
    }

    public void onDestroy(Context context, int key) {
        mSparseArray.get(key, mLifecycleEmptyImpl).onLifecycleDestroy();
        mSparseArray.remove(key);
        SensorEventManager.getInstance().unRegister();
    }

    private ILifecycleInterface mLifecycleEmptyImpl = new ILifecycleInterface() {
        @Override
        public void onLifecycleStop() {

        }

        @Override
        public void onLifecycleResume() {

        }

        @Override
        public void onLifecycleDestroy() {

        }
    };
}
