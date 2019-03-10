package com.chx.livemaker.manager.thread;

/**
 * Created by cangHX
 * on 2018/12/21  19:15
 */
public class ThreadPool {

    private ThreadPool(){}

    private static class Factory{
        private static final ThreadPool mInstance=new ThreadPool();
    }

    public static ThreadPool getInstance(){
        return Factory.mInstance;
    }



}
