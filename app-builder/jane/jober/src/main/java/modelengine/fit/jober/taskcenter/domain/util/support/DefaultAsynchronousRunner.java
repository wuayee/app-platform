/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.support;

import modelengine.fit.jober.taskcenter.domain.util.AsynchronousRunner;
import modelengine.fitframework.annotation.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 为 {@link AsynchronousRunner} 提供默认实现。
 *
 * @author 陈镕希
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
        this.executors = new ThreadPoolExecutor(INITIAL_SIZE,
                INITIAL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
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
