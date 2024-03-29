/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.server.http.support;

import com.huawei.fit.http.exception.AsyncTaskExecutionException;
import com.huawei.fit.http.exception.AsyncTaskNotAcceptedException;
import com.huawei.fit.http.exception.AsyncTaskNotFoundException;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * FIT 异步任务运行结果存储中心，单例模式。
 *
 * @author 王成 w00863339
 * @since 2023-11-17
 */
public enum AsyncTaskExecutor {
    INSTANCE;

    private static final Logger log = Logger.get(AsyncTaskExecutor.class);

    private static final int FIT_MAX_ASYNC_TASK_NUM = 1000;
    private static final int FIT_ASYNC_LONG_POLLING_DURATION_MILLISECONDS = 60000;

    private final ConcurrentHashMap<String, Future<Response>> store;
    private final ExecutorService asyncTaskExecutorService;

    AsyncTaskExecutor() {
        this.store = new ConcurrentHashMap<>();
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
     * @param taskId 表示待提交的异步任务的唯一标识的 {@link String}。
     * @param task 表示待提交的异步任务的 {@link Callable}{@code <}{@link Response}{@code >}。
     * @return 表示提交异步任务后的状态码的 {@code int}。
     */
    public int submit(String taskId, Callable<Response> task) {
        int code = ResponseMetadataV2.CODE_OK;
        try {
            Future<Response> responseFuture = this.asyncTaskExecutorService.submit(task);
            // Task ID重复时，输出warn日志并覆盖已有任务
            if (this.store.containsKey(taskId)) {
                log.warn("Duplicate task ID found. [taskId={}]", taskId);
            }
            this.addNewTask(taskId, responseFuture);
            log.debug("Async task added. [taskId={}, pendingTasks={}]", taskId, this.store.keySet());
        } catch (RejectedExecutionException e) {
            code = AsyncTaskNotAcceptedException.CODE;
        }
        return code;
    }

    /**
     * 长轮询异步任务。
     *
     * @param taskId 表示待长轮询的任务的唯一标识的 {@link String}。
     * @return 表示长轮询的结果的 {@link Optional}{@code <}{@link Response}{@code >}。
     * @throws AsyncTaskNotFoundException 当指定的异步任务无法找到时。
     * @throws AsyncTaskExecutionException 当指定的异步任务执行过程中发生异常时。
     */
    public Optional<Response> longPoll(String taskId)
            throws AsyncTaskNotFoundException, AsyncTaskExecutionException {
        log.debug("Async task started polling. [taskId={}, pendingTasks={}]", taskId, this.store.keySet());
        if (taskId == null || Objects.equals(taskId, StringUtils.EMPTY)) {
            String message = "No task ID specified.";
            log.warn(message);
            throw new AsyncTaskNotFoundException(message);
        }

        Future<Response> responseFuture = this.store.get(taskId);
        if (responseFuture == null) {
            String message = StringUtils.format("Specified task ID not found. [taskId={0}]", taskId);
            log.warn(message);
            throw new AsyncTaskNotFoundException(message);
        }

        try {
            Response response = responseFuture.get(FIT_ASYNC_LONG_POLLING_DURATION_MILLISECONDS, TimeUnit.MILLISECONDS);
            // 延迟清除已经结束的任务结果
            this.store.remove(taskId);
            log.debug("Async task removed. [taskId={}, pendingTasks={}]", taskId, this.store.keySet());
            return Optional.of(response);
        } catch (TimeoutException | InterruptedException e) {
            return Optional.empty();
        } catch (ExecutionException e) {
            log.warn("Async task execution failure. [taskId={}, cause={}]", taskId, e.getMessage());
            throw new AsyncTaskExecutionException(e.getCause());
        }
    }

    private void addNewTask(String taskId, Future<Response> responseFuture) {
        this.store.put(taskId, responseFuture);
    }
}
