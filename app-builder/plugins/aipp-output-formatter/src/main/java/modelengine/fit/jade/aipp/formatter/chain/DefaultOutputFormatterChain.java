/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.chain;

import modelengine.fit.jade.aipp.formatter.OutputFormatter;
import modelengine.fit.jade.aipp.formatter.OutputFormatterChain;
import modelengine.fit.jade.aipp.formatter.OutputMessage;
import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.LazyLoader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link OutputFormatter} 职责链默认实现。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
@Component
public class DefaultOutputFormatterChain implements OutputFormatterChain {
    private final BeanContainer container;
    private final LazyLoader<List<OutputFormatter>> chain;

    DefaultOutputFormatterChain(BeanContainer container) {
        this.container = Validation.notNull(container, "The container cannot be null.");
        this.chain = new LazyLoader<>(() -> this.container.all(OutputFormatter.class)
                .stream()
                .map(BeanFactory::<OutputFormatter>get)
                .collect(Collectors.toList()));
    }

    @Override
    public List<OutputFormatter> get() {
        return this.chain.get();
    }

    @Override
    public Optional<ResponsibilityResult> handle(Object data) {
        for (OutputFormatter formatter : this.get()) {
            Optional<OutputMessage> outputMessage = formatter.format(data);
            // 第一个匹配的格式化器
            if (outputMessage.isPresent()) {
                return Optional.of(new ResponsibilityResult(outputMessage.get(), formatter.name()));
            }
        }
        return Optional.empty();
    }
}
