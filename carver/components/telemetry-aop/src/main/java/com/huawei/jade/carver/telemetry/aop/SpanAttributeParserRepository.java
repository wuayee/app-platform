/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.LazyLoader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表达式解析器的仓库。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@Component
public class SpanAttributeParserRepository {
    private final BeanContainer container;
    private final LazyLoader<List<SpanAttributeParser>> parser;

    public SpanAttributeParserRepository(BeanContainer container) {
        this.container = Validation.notNull(container, "The container cannot be null.");
        this.parser = new LazyLoader<>(() -> this.container.all(SpanAttributeParser.class).stream()
                .map(BeanFactory::<SpanAttributeParser>get).collect(Collectors.toList()));
    }

    /**
     * 获取表达式解析器。
     *
     * @return 表示表达式解析器列表的 {@link List}{@code <}{@link SpanAttributeParser}{@code >}。
     */
    public List<SpanAttributeParser> get() {
        return this.parser.get();
    }
}
