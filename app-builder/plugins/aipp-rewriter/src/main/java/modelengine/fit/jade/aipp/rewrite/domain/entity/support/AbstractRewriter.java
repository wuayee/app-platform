/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.entity.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Nonnull;

import java.util.List;

/**
 * 表示 {@link Rewriter} 的抽象实现，算子步骤如下：
 * <ol>
 *     <li>组装提示词；</li>
 *     <li>调用模型；</li>
 *     <li>解析结果。</li>
 * </ol>
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public abstract class AbstractRewriter implements Rewriter {
    private final ChatModel modelService;

    /**
     * 创建 {@link AbstractRewriter} 的实例。
     *
     * @param modelService 表示模型服务的 {@link ChatModel}。
     * @throws IllegalArgumentException 当 {@code modelService} 为 {@code null} 时。
     */
    protected AbstractRewriter(ChatModel modelService) {
        this.modelService = notNull(modelService, "The model service cannot be null.");
    }

    @Override
    public final List<String> invoke(RewriteParam input) {
        notNull(input, "The rewrite param cannot be null.");
        String prompt = this.preparePrompt(input);
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.add(new HumanMessage(prompt));
        Choir<ChatMessage> answer = this.modelService.generate(chatMessages, input.getChatOption());
        return this.parseOutput(input, answer.blockAll().get(0));
    }

    /**
     * 根据输入参数准备提示词。
     *
     * @param param 表示重写参数的 {@link RewriteParam}。
     * @return 表示提示词的 {@link String}。
     */
    protected abstract String preparePrompt(@Nonnull RewriteParam param);

    /**
     * 根据输入和模型结果解析结果。
     *
     * @param param 表示重写参数的 {@link RewriteParam}。
     * @param answer 表示模型回复的 {@link ChatMessage}。
     * @return 表示重写问题的 {@link List}{@code <}{@link String}{@code >}。
     */
    protected abstract List<String> parseOutput(@Nonnull RewriteParam param, @Nonnull ChatMessage answer);
}