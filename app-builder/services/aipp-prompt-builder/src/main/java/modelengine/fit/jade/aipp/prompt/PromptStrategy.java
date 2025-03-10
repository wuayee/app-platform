/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

/**
 * 提示词构造策略。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
public enum PromptStrategy {
    /**
     * 溯源提示词构造策略。
     */
    REFERENCE("reference"),

    /**
     * 自定义提示词构造策略。
     */
    CUSTOM("custom");

    private final String value;

    PromptStrategy(String value) {
        this.value = value;
    }

    /**
     * 获取提示词构造策略名称。
     *
     * @return 表示提示词构造策略名称的 {@link String}。
     */
    public String value() {
        return this.value;
    }
}
