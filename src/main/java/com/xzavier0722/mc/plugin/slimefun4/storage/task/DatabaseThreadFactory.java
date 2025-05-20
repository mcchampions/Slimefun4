package com.xzavier0722.mc.plugin.slimefun4.storage.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseThreadFactory implements ThreadFactory {
    private final AtomicInteger threadCount = new AtomicInteger(0);
    private String prefix = "SF-Database-Thread #";

    public DatabaseThreadFactory() {}

    public DatabaseThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {

        return new Thread(r, prefix + threadCount.getAndIncrement());
    }
}
