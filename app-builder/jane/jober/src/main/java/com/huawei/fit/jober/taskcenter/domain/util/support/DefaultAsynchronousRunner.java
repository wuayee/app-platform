/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jober.taskcenter.domain.util.AsynchronousRunner;
import com.huawei.fitframework.annotation.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * 为 {@link AsynchronousRunner} 提供默认实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-30
 */
@Component
public class DefaultAsynchronousRunner implements AsynchronousRunner {
    /**
     * 返回 {@link DefaultAsynchronousRunner} 的唯一实例。
     */
    public static final DefaultAsynchronousRunner INSTANCE = new DefaultAsynchronousRunner();

    private static final int INITIAL_SIZE = 10;

    private final ExecutorService executors;

    private DefaultAsynchronousRunner() {
        this.executors = Executors.newFixedThreadPool(INITIAL_SIZE);
    }

    @Override
    public void run(Runnable... actions) {
        Optional.ofNullable(actions)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .forEach(this.executors::submit);
    }
}
