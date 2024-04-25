/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 表示工具的查询服务。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
public interface ToolSchemaQueryService {
    /**
     * 搜索指定名字的工具。
     *
     * @param group 表示工具所属的组名字的 {@link String}。
     * @param toolName 表示工具名字的 {@link String}。
     * @return 表示工具的格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.tool.schema.search")
    Map<String, Object> search(String group, String toolName);
}
