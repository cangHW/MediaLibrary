package com.chx.livemaker.manager.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by cangHX
 * on 2018/12/21  17:42
 */
public class ThreadManager {

//    private static final RunnableStore STORE = RunnableStore.getInstance();
//    private static final ThreadPool POOL = ThreadPool.getInstance();
    private static ScheduledExecutorService mService=Executors.newScheduledThreadPool(3);

    private ThreadManager() {
    }

    private static class Factory {
        private static final ThreadManager mInstance = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Factory.mInstance;
    }

    public void addRunnable(Runnable runnable) {
        try {
            mService.execute(runnable);
        }catch (Exception e){
            e.toString();
        }
    }

    public void finishAllRunnable(){
        //TODO 后面自定义线程池
        mService.shutdownNow();
        mService=Executors.newScheduledThreadPool(3);
    }
}
