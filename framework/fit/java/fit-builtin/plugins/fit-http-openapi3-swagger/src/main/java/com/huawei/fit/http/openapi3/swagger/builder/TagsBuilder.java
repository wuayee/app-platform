/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.builder;

import com.huawei.fit.http.openapi3.swagger.EntityBuilder;
import com.huawei.fit.http.openapi3.swagger.entity.Tag;
import com.huawei.fit.http.server.HttpDispatcher;
import com.huawei.fit.http.server.HttpHandlerGroup;
import com.huawei.fitframework.ioc.BeanContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link List}{@code <}{@link Tag}{@code >} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class TagsBuilder extends AbstractBuilder implements EntityBuilder<List<Tag>> {
    TagsBuilder(BeanContainer container) {
        super(container);
    }

    @Override
    public List<Tag> build() {
        Collection<HttpHandlerGroup> groups = this.getHttpDispatcher()
                .map(HttpDispatcher::getHttpHandlerGroups)
                .map(Map::values)
                .orElseGet(Collections::emptyList);
        return groups.stream()
                .filter(group -> group.getHandlers().stream().anyMatch(handler -> !this.isHandlerIgnored(handler)))
                .map(group -> Tag.custom().name(group.getName()).description(group.getDescription()).build())
                .collect(Collectors.toList());
    }
}
