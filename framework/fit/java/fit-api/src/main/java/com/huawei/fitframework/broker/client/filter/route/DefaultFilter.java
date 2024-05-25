/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.route;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 使用泛服务的默认实现的路由的过滤器。
 *
 * @author 季聿阶 j00559309
 * @since 2021-08-16
 */
public class DefaultFilter extends AbstractFilter {
    /** {@link DefaultFilter} 的预置实例。 */
    public static final DefaultFilter INSTANCE = new DefaultFilter();

    /**
     * 隐藏默认构造方法，如需使用，请直接使用 {@link #INSTANCE 预置实例}。
     */
    private DefaultFilter() {}

    @Override
    protected List<? extends FitableMetadata> route(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions) {
        List<? extends FitableMetadata> filteredFitables;
        String defaultId = genericable.route().defaultFitable();
        if (StringUtils.isNotBlank(defaultId)) {
            filteredFitables = toFilterFitables.stream()
                    .filter(fitable -> Objects.equals(fitable.id(), defaultId))
                    .collect(Collectors.toList());
        } else {
            filteredFitables = toFilterFitables;
        }
        return filteredFitables;
    }

    @Override
    public String toString() {
        return "DefaultFilter{}";
    }
}
