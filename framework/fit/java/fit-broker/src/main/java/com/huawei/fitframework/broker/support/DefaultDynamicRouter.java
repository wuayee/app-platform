/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import com.huawei.fitframework.broker.DynamicRouter;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示 {@link DynamicRouter} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public class DefaultDynamicRouter implements DynamicRouter {
    @Override
    public List<Fitable> route(Genericable genericable, InvocationContext context, Object[] args) {
        if (context.routingFilter() == null) {
            return genericable.fitables();
        }
        return context.routingFilter().filter(genericable, genericable.fitables(), args, context.filterExtensions())
                .stream()
                .filter(Objects::nonNull)
                .map(metadata -> this.cast(metadata, genericable.fitables()))
                .collect(Collectors.toList());
    }

    private Fitable cast(FitableMetadata metadata, List<Fitable> scope) {
        if (metadata instanceof Fitable) {
            return ObjectUtils.cast(metadata);
        }
        for (Fitable fitable : scope) {
            if (Objects.equals(metadata.toUniqueId(), fitable.toUniqueId())) {
                return fitable;
            }
        }
        throw new IllegalStateException(StringUtils.format(
                "Failed to cast FitableMetadata to Fitable after dynamic routing. [id={0}]",
                metadata.toUniqueId()));
    }
}
