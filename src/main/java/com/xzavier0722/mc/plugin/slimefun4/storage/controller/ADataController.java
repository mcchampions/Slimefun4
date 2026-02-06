package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordSet;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.ScopeKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.task.DatabaseThreadFactory;
import com.xzavier0722.mc.plugin.slimefun4.storage.task.QueuedWriteTask;
import city.norain.slimefun4.utils.SlimefunPoolExecutor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ADataController} 是 Slimefun 数据库控制器的抽象类，
 * 提供了对数据源适配器的访问和数据操作的基本方法。
 * <br/>
 * 该类提供了对数据库的增删查改操作以及异步读写的支持。
 */
@Slf4j
public abstract class ADataController {
    private final DataType dataType;
    private final Map<ScopeKey, QueuedWriteTask> scheduledWriteTasks;
    private final ScopedLock lock;

    private volatile IDataSourceAdapter<?> dataAdapter;

    protected ExecutorService readExecutor;

    protected ExecutorService writeExecutor;

    protected ExecutorService serialWriteExecutor;

    protected ExecutorService callbackExecutor;

    private volatile boolean destroyed;

    protected final Logger logger;

    protected ADataController(DataType dataType) {
        this.dataType = dataType;
        scheduledWriteTasks = new ConcurrentHashMap<>();
        lock = new ScopedLock();
        logger = Logger.getLogger("SF-" + dataType.name() + "-Controller");
    }

    /**
     * 初始化 {@link ADataController}
     */
    @OverridingMethodsMustInvokeSuper
    public void init(IDataSourceAdapter<?> dataAdapter, int maxReadThread, int maxWriteThread) {
        this.dataAdapter = dataAdapter;
        dataAdapter.initStorage(dataType);
        dataAdapter.patch();
        readExecutor = new SlimefunPoolExecutor(
                "SF-" + dataType.name() + "-Read-Executor",
                maxReadThread,
                maxReadThread,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DatabaseThreadFactory("SF-" + dataType.name() + "-Read-Thread #"));

        writeExecutor = new SlimefunPoolExecutor(
                "SF-" + dataType.name() + "-Write-Executor",
                Math.max(maxWriteThread - 1, 1),
                Math.max(maxWriteThread - 1, 1),
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DatabaseThreadFactory("SF-" + dataType.name() + "-Write-Thread #"));

        if (maxWriteThread > 1) {
            serialWriteExecutor = new SlimefunPoolExecutor(
                    "SF-" + dataType.name() + "-SerialWrite-Executor",
                    1,
                    1,
                    10,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new DatabaseThreadFactory("SF-" + dataType.name() + "-SerialWrite-Thread #"));
        }

        callbackExecutor = new SlimefunPoolExecutor(
                "SF-" + dataType.name() + "-Callback-Executor",
                1,
                Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DatabaseThreadFactory("SF-" + dataType.name() + "-Callback-Thread #"));
    }

    /**
     * 正常关闭 {@link ADataController}
     */
    @OverridingMethodsMustInvokeSuper
    public void shutdown() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        readExecutor.shutdownNow();
        callbackExecutor.shutdownNow();

        try {
            float totalTask = scheduledWriteTasks.size();
            var pendingTask = scheduledWriteTasks.size();

            while (pendingTask > 0) {
                String doneTaskPercent = String.format("%.1f", (totalTask - pendingTask) / totalTask * 100);
                logger.log(Level.INFO, "数据保存中，请稍候... 剩余 {0} 个任务 ({1}%)", new Object[]{pendingTask, doneTaskPercent});
                TimeUnit.SECONDS.sleep(1);
                pendingTask = scheduledWriteTasks.size();
            }

            logger.info("数据保存完成.");
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Exception thrown while saving data: ", e);
        }
        writeExecutor.shutdownNow();
        dataAdapter = null;
    }

    protected void scheduleDeleteTask(ScopeKey scopeKey, RecordKey key, boolean forceScopeKey) {
        scheduleWriteTask(
                scopeKey,
                key,
                () -> dataAdapter.deleteData(key),
                forceScopeKey);
    }

    protected void scheduleWriteTask(ScopeKey scopeKey, RecordKey key, RecordSet data, boolean forceScopeKey) {
        scheduleWriteTask(scopeKey, key, () -> dataAdapter.setData(key, data), forceScopeKey);
    }

    protected void scheduleWriteTask(ScopeKey scopeKey, RecordKey key, Runnable task, boolean forceScopeKey) {
        lock.lock(scopeKey);

        // log.info("schedule write scope [{}], key [{}]", scopeKey, key);

        try {
            ScopeKey scopeToUse = forceScopeKey ? scopeKey : key;
            QueuedWriteTask queuedTask = scheduledWriteTasks.get(scopeKey);
            if (queuedTask == null && scopeKey != scopeToUse) {
                queuedTask = scheduledWriteTasks.get(scopeToUse);
            }

            if (queuedTask != null && queuedTask.queue(key, task)) {
                return;
            }

            queuedTask = new QueuedWriteTask() {
                @Override
                protected void onSuccess() {
                    scheduledWriteTasks.remove(scopeToUse);
                }

                @Override
                protected void onError(Throwable e) {
                    Slimefun.logger()
                            .log(
                                    Level.SEVERE,
                                    "[" + Thread.currentThread().getName()
                                            + "] Exception thrown while executing write task: ",
                                    e);
                }
            };
            queuedTask.queue(key, task);
            scheduledWriteTasks.put(scopeToUse, queuedTask);

            if (serialWriteExecutor != null && key.getScope().isSerial()) {
                serialWriteExecutor.submit(queuedTask);
            } else {
                writeExecutor.submit(queuedTask);
            }
        } finally {
            lock.unlock(scopeKey);
        }
    }

    protected void checkDestroy() {
    }

    protected <T> void invokeCallback(IAsyncReadCallback<T> callback, T result) {
        if (callback == null) {
            return;
        }

        Runnable cb;
        if (result == null) {
            cb = callback::onResultNotFound;
        } else {
            cb = () -> callback.onResult(result);
        }

        if (callback.runOnMainThread()) {
            Slimefun.runSync(cb);
        } else {
            callbackExecutor.submit(cb);
        }
    }

    protected void scheduleReadTask(Runnable run) {
        checkDestroy();
        readExecutor.submit(run);
    }

    protected void scheduleWriteTask(Runnable run) {
        checkDestroy();
        writeExecutor.submit(run);
    }

    protected List<RecordSet> getData(RecordKey key) {
        return getData(key, false);
    }

    protected List<RecordSet> getData(RecordKey key, boolean distinct) {
        return dataAdapter.getData(key, distinct);
    }

    protected void setData(RecordKey key, RecordSet data) {
        dataAdapter.setData(key, data);
    }

    protected void deleteData(RecordKey key) {
        dataAdapter.deleteData(key);
    }

    protected void abortScopeTask(ScopeKey key) {
        QueuedWriteTask task = scheduledWriteTasks.remove(key);
        if (task != null) {
            task.abort();
        }
    }

    public final DataType getDataType() {
        return dataType;
    }
}
