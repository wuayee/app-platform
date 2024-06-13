/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;
/**
 * Task 的枚举类。
 *
 * @author 李金绪 l00878072
 * @since 2024/6/12
 */
public enum TaskEnum {
    HUGGINGFACE("HUGGINGFACE"),

    LANGCHAIN("LANGCHAIN"),

    LLAMAINDEX("LLAMAINDEX"),

    AUTHORITY("AUTHORITY");

    private final String name;

    TaskEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}