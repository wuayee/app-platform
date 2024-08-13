/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.server.http.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.exception.AsyncTaskExecutionException;
import com.huawei.fit.http.exception.AsyncTaskNotAcceptedException;
import com.huawei.fit.http.exception.AsyncTaskNotFoundException;
import com.huawei.fit.serialization.http.HttpUtils;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.tlv.TlvUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * FIT 异步任务运行结果存储中心，单例模式。
 *
 * @author 王成
 * @since 2023-11-17
 */
public class AsyncTaskExecutor {
    /** 表示异步任务执行器的单例。 */
    public static final AsyncTaskExecutor INSTANCE = new AsyncTaskExecutor();

    private static final Logger log = Logger.get(AsyncTaskExecutor.class);

    private static final int FIT_MAX_ASYNC_TASK_NUM = 1000;

    private final ConcurrentHashMap<String, AsyncTaskCreator> creators;
    private final ExecutorService asyncTaskExecutorService;

    private AsyncTaskExecutor() {
        this.creators = new ConcurrentHashMap<>();
        this.asyncTaskExecutorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(FIT_MAX_ASYNC_TASK_NUM),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 提交异步任务。
     *
     * @param metadata 表示待提交的异步任务的 {@link RequestMetadata}。
     * @param task 表示待提交的异步任务的 {@link Supplier}{@code <}{@link Response}{@code >}。
     * @return 表示提交异步任务后的状态码的 {@code int}。
     */
    public int submit(RequestMetadata metadata, Supplier<Response> task) {
        notNull(metadata, "The request metadata cannot be null.");
        String workerId = TlvUtils.getWorkerId(metadata.tagValues());
        String workerInstanceId = TlvUtils.getWorkerInstanceId(metadata.tagValues());
        String asyncTaskId = HttpUtils.getAsyncTaskId(metadata.tagValues());
        if (StringUtils.isBlank(workerId) || StringUtils.isBlank(workerInstanceId)
                || StringUtils.isBlank(asyncTaskId)) {
            return AsyncTaskNotAcceptedException.CODE;
        }
        try {
            AsyncTaskCreator curCreator = this.getOrCreateAsyncTaskCreator(workerId, workerInstanceId);
            CompletableFuture<Response> responseFuture =
                    CompletableFuture.supplyAsync(task, this.asyncTaskExecutorService);
            responseFuture.thenRun(curCreator::notifyCompletion);
            curCreator.addTask(asyncTaskId, responseFuture);
            return ResponseMetadata.CODE_OK;
        } catch (RejectedExecutionException e) {
            return AsyncTaskNotAcceptedException.CODE;
        }
    }

    /**
     * 长轮询异步任务。
     *
     * @param sourceWorkerId 表示请求来源方的进程唯一标识的 {@link String}。
     * @param sourceWorkerInstanceId 标识请求来源方的进程实例唯一标识的 {@link String}。
     * @return 表示长轮询的结果的 {@link Optional}{@code <}{@link Response}{@code >}。
     * @throws AsyncTaskNotFoundException 当指定的异步任务无法找到时。
     * @throws AsyncTaskExecutionException 当指定的异步任务执行过程中发生异常时。
     */
    public Optional<Response> longPolling(String sourceWorkerId, String sourceWorkerInstanceId)
            throws AsyncTaskNotFoundException, AsyncTaskExecutionException {
        return this.getAsyncTaskCreator(sourceWorkerId, sourceWorkerInstanceId).longPolling();
    }

    private AsyncTaskCreator getOrCreateAsyncTaskCreator(String workerId, String workerInstanceId) {
        // 根据 workerId 获取任务提交者，如果任务来自一个新的提交者实例，则丢弃已有实例和附属的任务记录
        synchronized (this.creators) {
            AsyncTaskCreator creator = this.creators.get(workerId);
            if (creator == null || creator.isNotSameInstance(workerInstanceId)) {
                creator = new AsyncTaskCreator(workerInstanceId);
                AsyncTaskCreator obsoleteCreator = this.creators.put(workerId, creator);
                if (obsoleteCreator != null) {
                    log.warn("New worker instance detected. Discard obsolete instance tasks. "
                                    + "[workerId={}, obsoleteInstanceId={}, newInstanceId={}]",
                            workerId,
                            obsoleteCreator.getInstanceId(),
                            workerInstanceId);
                }
            }
            return creator;
        }
    }

    private AsyncTaskCreator getAsyncTaskCreator(String workerId, String workerInstanceId) {
        synchronized (this.creators) {
            AsyncTaskCreator creator = this.creators.get(workerId);
            if (creator == null || creator.isNotSameInstance(workerInstanceId)) {
                String message = StringUtils.format(
                        "Async task not found for current worker. [workerId={0}, instanceId={1}]",
                        workerId,
                        workerInstanceId);
                log.warn(message);
                throw new AsyncTaskNotFoundException(message);
            }
            return creator;
        }
    }
}
