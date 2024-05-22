/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 表示工具的执行服务。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
public interface ToolExecuteService {
    /**
     * 执行指定唯一名字的工具。
     *
     * @param uniqueName 表示指定工具唯一名字的 {@link String}。
     * @param jsonArgs 表示执行参数的 Json 字符串的 {@link String}。
     * @return 表示执行结果的 Json 字符串的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.tool.execute")
    String executeTool(String uniqueName, String jsonArgs);
}
