/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.GenericableExecutor;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.log.Logger;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link GenericableExecutor} 的多播调用实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-28
 */
public class MulticastGenericableExecutor implements GenericableExecutor {
    private static final Logger log = Logger.get(MulticastGenericableExecutor.class);

    private final GenericableExecutor executor;

    MulticastGenericableExecutor(GenericableExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Object execute(List<Fitable> fitables, InvocationContext context, Object[] args) {
        return fitables.stream().map(fitable -> {
            try {
                return this.executor.execute(Collections.singletonList(fitable), context, args);
            } catch (Throwable t) {
                log.warn("Failed to execute genericable executor while multicast, return null instead. [id={}]",
                        fitable.toUniqueId());
                return null;
            }
        }).reduce(context.accumulator()).orElse(null);
    }
}
