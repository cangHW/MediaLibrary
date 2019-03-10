package com.chx.livemaker.manager.thread;

/**
 * Created by cangHX
 * on 2018/12/21  19:14
 */
public class RunnableStore {

    private RunnableStore(){}

    private static class Factory{
        private static final RunnableStore mInstance=new RunnableStore();
    }

    public static RunnableStore getInstance(){
        return Factory.mInstance;
    }

}
