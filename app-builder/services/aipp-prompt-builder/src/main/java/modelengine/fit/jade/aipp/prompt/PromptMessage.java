/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modelengine.fitframework.inspection.Validation;

import java.util.Collections;
import java.util.Map;

/**
 * 提示词消息。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
@AllArgsConstructor
@Getter
public class PromptMessage {
    /**
     * 系统提示词。
     */
    private String systemMessage;

    /**
     * 用户提示词。
     */
    private String humanMessage;

    /**
     * 提示词元数据。
     */
    private Map<String, Object> metadata;

    /**
     * 通过系统提示词和用户提示词构造 {@link PromptMessage} 对象。
     *
     * @param systemMessage 表示系统提示词的 {@link String}。
     * @param humanMessage 表示用户提示词的 {@link String}。
     */
    public PromptMessage(String systemMessage, String humanMessage) {
        this.systemMessage = Validation.notNull(systemMessage, "The system message cannot be null.");
        this.humanMessage = Validation.notNull(humanMessage, "The human message cannot be null.");
        this.metadata = Collections.emptyMap();
    }
}
