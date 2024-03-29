/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.route;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 指定泛服务实现的别名的路由的过滤器。
 *
 * @author 季聿阶 j00559309
 * @since 2021-06-11
 */
public class AliasFilter extends AbstractFilter {
    private final List<String> aliases;

    public AliasFilter(String... aliases) {
        this(Stream.of(ObjectUtils.getIfNull(aliases, () -> new String[0])).collect(Collectors.toList()));
    }

    /**
     * 创建别名的路由过滤器。
     *
     * @param aliases 表示用于初始化过滤器的别名集合的 {@link List}{@code <}{@link String}{@code >}。
     */
    public AliasFilter(List<String> aliases) {
        this.aliases = ObjectUtils.getIfNull(aliases, Collections::<String>emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Validation.isTrue(CollectionUtils.isNotEmpty(this.aliases), "No valid alias to instantiate AliasFilter.");
    }

    @Override
    protected List<? extends FitableMetadata> route(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args) {
        return toFilterFitables.stream().filter(this::containsAnyAlias).collect(Collectors.toList());
    }

    private boolean containsAnyAlias(FitableMetadata fitable) {
        Set<String> theSameAliases = CollectionUtils.intersect(fitable.aliases().all(), this.aliases);
        return !theSameAliases.isEmpty();
    }

    @Override
    public String toString() {
        return "AliasFilter{" + "aliases=" + this.aliases + '}';
    }
}
