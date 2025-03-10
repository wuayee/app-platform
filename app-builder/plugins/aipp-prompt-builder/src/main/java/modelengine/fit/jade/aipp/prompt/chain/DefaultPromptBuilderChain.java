/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.chain;

import modelengine.fit.jade.aipp.prompt.PromptBuilder;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.LazyLoader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提示器构造器职责链的默认实现。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
@Component
public class DefaultPromptBuilderChain implements PromptBuilderChain {
    private final LazyLoader<List<PromptBuilder>> chain;

    DefaultPromptBuilderChain(BeanContainer container) {
        this.chain = new LazyLoader<>(() -> container.all(PromptBuilder.class)
                .stream()
                .map(BeanFactory::<PromptBuilder>get)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<PromptMessage> build(UserAdvice userAdvice, Map<String, Object> context) {
        for (PromptBuilder promptBuilder : this.chain.get()) {
            Optional<PromptMessage> promptMessage = promptBuilder.build(userAdvice, context);
            // 第一个匹配的提示器构造器
            if (promptMessage.isPresent()) {
                return promptMessage;
            }
        }
        return Optional.empty();
    }
}
