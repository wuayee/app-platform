/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.constant;

/**
 * 提示词构造器内部常量。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
public interface InternalConstant {
    /**
     * 溯源提示词文件国际化的键。
     */
    String TEMPLATE_LOCALE_KEY = "aipp.prompt.builder.reference.template";

    /**
     * 中文溯源提示词文件。
     */
    String REFERENCE_TEMPLATE_ZH = "/prompt/reference_template_zh.txt";

    /**
     * 英文溯源提示词文件。
     */
    String REFERENCE_TEMPLATE_EN = "/prompt/reference_template_en.txt";

    /**
     * 提示词分隔符。
     */
    String BLOCK_SEPARATOR = "\n\n";

    /**
     * 人设与问题背景国际化的键。
     */
    String BACKGROUND_KEY = "aipp.prompt.builder.background";

    /**
     * 大模型节点的知识栏中，引用知识检索节点的数量上限。
     */
    int KNOWLEDGE_NODE_LIMIT = 5;

    /**
     * 大模型节点的知识栏中，引用知识文本的长度上限。
     */
    int KNOWLEDGE_CONTENT_LIMIT = 16384;
}
