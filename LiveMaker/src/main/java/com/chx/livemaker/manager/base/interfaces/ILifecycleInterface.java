package com.chx.livemaker.manager.base.interfaces;

/**
 * Created by cangHX
 * on 2018/12/14  14:56
 */
public interface ILifecycleInterface {

    void onLifecycleStop();

    void onLifecycleResume();

    void onLifecycleDestroy();
}
