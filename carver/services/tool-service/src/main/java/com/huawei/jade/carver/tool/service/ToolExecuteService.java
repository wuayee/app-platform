/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.service;

import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 表示工具的执行服务。
 *
 * @author 季聿阶
 * @since 2024-04-08
 */
public interface ToolExecuteService {
    /**
     * 执行指定工具。
     *
     * @param uniqueName 表示指定工具唯一名字的 {@link String}。
     * @param jsonArgs 表示执行参数的 Json 字符串的 {@link String}。
     * @return 表示执行结果的 Json 字符串的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.execute.json")
    String executeTool(String uniqueName, String jsonArgs);

    /**
     * 执行指定工具。
     *
     * @param uniqueName 表示指定工具唯一名字的 {@link String}。
     * @param jsonObjectArgs 表示执行参数的 Json 对象的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示执行结果的 {@link Object}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.execute.jsonObject")
    Object executeTool(String uniqueName, Map<String, Object> jsonObjectArgs);
}
