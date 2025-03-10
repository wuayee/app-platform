/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop;

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
public class SpanAttrParserRepository {
    private final BeanContainer container;
    private final LazyLoader<List<SpanAttrParser>> parser;

    public SpanAttrParserRepository(BeanContainer container) {
        this.container = Validation.notNull(container, "The container cannot be null.");
        this.parser = new LazyLoader<>(() -> this.container.all(SpanAttrParser.class).stream()
                .map(BeanFactory::<SpanAttrParser>get).collect(Collectors.toList()));
    }

    /**
     * 获取表达式解析器。
     *
     * @return 表示表达式解析器列表的 {@link List}{@code <}{@link SpanAttrParser}{@code >}。
     */
    public List<SpanAttrParser> get() {
        return this.parser.get();
    }
}
