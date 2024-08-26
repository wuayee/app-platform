/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerFilterSupplier;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示获取默认的 {@link HttpServerFilter} 实例列表的提供器。
 *
 * @author 季聿阶
 * @since 2023-01-10
 */
public class DefaultHttpServerFilterSupplier implements HttpServerFilterSupplier {
    @Override
    public List<HttpServerFilter> get(BeanContainer container) {
        List<HttpServerFilter> pluginFilters =
                notNull(container, "The bean container cannot be null.").factories(HttpServerFilter.class)
                        .stream()
                        .map(BeanFactory::<HttpServerFilter>get)
                        .filter(httpServerFilter -> httpServerFilter.scope() == Scope.PLUGIN)
                        .collect(Collectors.toList());
        List<HttpServerFilter> globalFilters = container.all(HttpServerFilter.class)
                .stream()
                .map(BeanFactory::<HttpServerFilter>get)
                .filter(filter -> filter.scope() == Scope.GLOBAL)
                .collect(Collectors.toList());
        List<HttpServerFilter> allFilters = new ArrayList<>();
        allFilters.addAll(pluginFilters);
        allFilters.addAll(globalFilters);
        return allFilters;
    }
}
