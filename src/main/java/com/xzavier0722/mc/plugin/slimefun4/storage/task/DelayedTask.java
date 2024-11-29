package com.xzavier0722.mc.plugin.slimefun4.storage.task;

import java.util.concurrent.TimeUnit;

public class DelayedTask {
    private final Runnable task;
    private long runAfter;
    private boolean executed;

    public DelayedTask(long delay, TimeUnit unit, Runnable task) {
        this.task = task;
        setRunAfter(delay, unit);
    }

    public synchronized void setRunAfter(long delay, TimeUnit unit) {
        runAfter = System.currentTimeMillis() + unit.toMillis(delay);
    }

    public synchronized boolean tryRun() {
        if (System.currentTimeMillis() < runAfter) {
            return false;
        }

        executed = true;
        task.run();
        return true;
    }

    public synchronized boolean isExecuted() {
        return executed;
    }

    public void runUnsafely() {
        executed = true;
        task.run();
    }
}
