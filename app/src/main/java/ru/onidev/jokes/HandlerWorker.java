package ru.onidev.jokes;


import android.os.Handler;
import android.os.HandlerThread;

class HandlerWorker extends HandlerThread {

    private Handler mWorkerHandler;

    HandlerWorker(String name) {
        super(name);
    }

    void postTask(Runnable task) {
        mWorkerHandler.post(task);
    }

    void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }
}
