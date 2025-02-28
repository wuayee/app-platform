/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.code;

import modelengine.jade.common.code.RetCode;

/**
 * 提示词构造器错误码。
 *
 * @author 刘信宏
 * @since 2024-12-04
 */
public enum PromptBuilderRetCode implements RetCode {
    /**
     * 知识库数量超过限制。
     */
    PROMPT_BUILDER_KNOWLEDGE_COUNT_LIMIT(131003001,
            "The number of referenced knowledge retrieval nodes exceeds the upper limit. [current={0}, limit={1}]"),

    /**
     * 知识文本长度超过限制。
     */
    PROMPT_BUILDER_KNOWLEDGE_CONTENT_LIMIT(131003002,
            "The length of knowledge content exceeds the upper limit. [current={0}, limit={1}]"),

    /**
     * 应用的知识库为空。
     */
    PROMPT_BUILDER_KNOWLEDGE_EMPTY(131003003, "The referenced knowledge cannot be empty.");

    private final int code;
    private final String msg;

    PromptBuilderRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
