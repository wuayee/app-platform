/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.entity.support;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;
import modelengine.fitframework.inspection.Nonnull;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link Rewriter} 的自定义实现。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public class CustomRewriter extends AbstractRewriter {
    /**
     * 创建 {@link CustomRewriter} 的实例。
     *
     * @param modelService 表示模型服务的 {@link ChatModel}。
     * @throws IllegalArgumentException 当 {@code modelService} 为 {@code null} 时。
     */
    public CustomRewriter(ChatModel modelService) {
        super(modelService);
    }

    @Override
    public RewriteStrategy strategy() {
        return RewriteStrategy.CUSTOM;
    }

    @Override
    protected String preparePrompt(@Nonnull RewriteParam param) {
        return new DefaultStringTemplate(param.getTemplate()).render(param.getVariables());
    }

    @Override
    protected List<String> parseOutput(@Nonnull RewriteParam param, @Nonnull ChatMessage answer) {
        return Collections.singletonList(answer.text());
    }
}