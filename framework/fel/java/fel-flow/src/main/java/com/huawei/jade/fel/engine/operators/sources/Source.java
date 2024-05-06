/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.sources;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数据发射源实现，支持注册多个监听器。仅支持主动逐个发射数据。
 *
 * @param <T> 表示数据源的业务数据类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
public class Source<T> implements Emitter<T, FlowSession> {
    private static final Integer MAXIMUM_POOL_SIZE = 50;
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, MAXIMUM_POOL_SIZE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    private final List<EmitterListener<T, FlowSession>> handlers = new ArrayList<>();

    @Override
    public void register(EmitterListener<T, FlowSession> handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    @Override
    public void emit(T data, FlowSession session) {
        handlers.forEach(handler -> THREAD_POOL.execute(() -> handler.handle(data, session)));
    }
}
