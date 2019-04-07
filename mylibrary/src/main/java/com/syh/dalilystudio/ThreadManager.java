package com.syh.dalilystudio;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by shenyh on 2016-06-14.
 */
public class ThreadManager {
    public static final String TAG = "ThreadManager";

    public static final int THREAD_UI = 0;
    public static final int THREAD_BG = 1;

    private static ThreadManager instance;

    private ThreadManager() {
        init();
    }
    public static ThreadManager getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }

    private HandlerThread bgThread;
    private Handler mMainHandler;
    private Handler mBgHandler;

    private void init() {
        mMainHandler = new Handler(Looper.getMainLooper());
        bgThread = new HandlerThread("bg");
        bgThread.start();
        mBgHandler = new Handler(bgThread.getLooper());
    }

    public boolean runningOnUIThread(){
        return Looper.getMainLooper()==Looper.myLooper();
    }

    public void postOnBgThread(Runnable runnable, long delayMillis) {
        if (mBgHandler != null) {
            mBgHandler.postDelayed(runnable, delayMillis);
        } else {
            LogUtil.d(TAG, "mBgHandler is null");
        }
    }

    public void removeFromBgThread(Runnable runnable) {
        if (mBgHandler != null) {
            mBgHandler.removeCallbacks(runnable);
        }
    }

    public void removeFromUIThread(Runnable runnable) {
        if (mMainHandler != null) {
            mMainHandler.removeCallbacks(runnable);
        }
    }

    public void postOnUIThread(Runnable runnable, long delayMillis) {
        if (mMainHandler != null) {
            mMainHandler.postDelayed(runnable, delayMillis);
        } else {
            LogUtil.d(TAG, "mMainHandler is null");
        }
    }

    //IO等耗时 操作，非网络操作
    public void postOnBgThread(Runnable runnable) {
        postOnBgThread(runnable, 0l);
    }

    public void postOnUIThread(Runnable runnable) {
        postOnUIThread(runnable, 0l);
    }

    private void onDestroy() {
        mMainHandler = null;
        mBgHandler = null;
        instance = null;
    }

    public static void destroy() {
        synchronized (ThreadManager.class) {

            if (instance != null) {
                instance.onDestroy();
            }
        }
    }
}
