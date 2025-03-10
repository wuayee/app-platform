/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.constant;

/**
 * 提示词构造器公共常量。
 *
 * @author 刘信宏
 * @since 2024-12-06
 */
public interface Constant {
    /**
     * 模型节点知识栏数据的键。
     */
    String KNOWLEDGE_CONTEXT_KEY = "knowledgeBases";

    /**
     * 提示词元数据。
     */
    String PROMPT_METADATA_KEY = "knowledgeMetadata";
}
