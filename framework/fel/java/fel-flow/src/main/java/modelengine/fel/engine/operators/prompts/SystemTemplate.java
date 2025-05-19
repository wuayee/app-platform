/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.operators.prompts;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.template.StringTemplate;
import modelengine.fel.core.template.support.SystemMessageTemplate;
import modelengine.fel.core.util.Tip;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

/**
 * 系统提示词模板。仅使用文本数据渲染模板，忽略媒体数据。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class SystemTemplate implements PromptTemplate<Tip> {
    private final SystemMessageTemplate messageTemplate;

    /**
     * 使用 mustache 模板初始化 {@link SystemTemplate}。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null}、空字符串或只有空白字符的字符串时。
     */
    public SystemTemplate(StringTemplate template) {
        Validation.notNull(template, "Template cannot be null.");
        this.messageTemplate = new SystemMessageTemplate(template);
    }

    @Override
    public Prompt invoke(Tip input) {
        ChatMessage sysMessage = this.messageTemplate.render(input.freeze());
        if (StringUtils.isBlank(sysMessage.text())) {
            return new ChatMessages();
        }
        return ChatMessages.from(sysMessage);
    }
}
