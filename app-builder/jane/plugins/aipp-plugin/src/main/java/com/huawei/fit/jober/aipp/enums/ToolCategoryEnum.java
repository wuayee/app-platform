/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

/**
 * Task 的枚举类。
 *
 * @author 李金绪 l00878072
 * @since 2024/6/12
 */
public enum ToolCategoryEnum {
    HUGGINGFACE("HuggingFace"),

    LANGCHAIN("LangChain"),

    LLAMAINDEX("LlamaIndex"),

    AUTHORITY("Authority");

    private final String name;

    ToolCategoryEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}