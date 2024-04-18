/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

/**
 * 表示可以处理 Json 入参和返回值的工具。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface JsonTool extends Tool {
    /**
     * 使用 Json 格式的参数调用工具。
     *
     * @param jsonArgs 表示调用工具的 Json 格式的参数的 {@link String}。
     * @return 表示调用工具的 Json 格式的结果的 {@link String}。
     */
    String callByJson(String jsonArgs);
}
