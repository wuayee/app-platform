/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.List;

/**
 * 动态条件查询插件的类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public class AppQuery extends ToolQuery {
    /**
     * 用所有参数构造 {@link AppQuery}。
     *
     * @param toolName 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @param version 表示工具的版本的 {@link String}。
     */
    public AppQuery(String toolName, List<String> includeTags,
            List<String> excludeTags, Integer pageNum, Integer limit, String version) {
        super(toolName, includeTags, excludeTags, pageNum, limit, version);
    }

    /**
     * 空参构造 {@link AppQuery}。
     */
    public AppQuery() {}
}
