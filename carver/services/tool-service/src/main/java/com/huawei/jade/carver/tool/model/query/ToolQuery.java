/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.model.query;

import com.huawei.fitframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 动态条件查询的类
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolQuery {
    /**
     * 表示工具名称。
     * <p>构造条件时按需传入。</p>
     */
    private String toolName;

    /**
     * 表示需要包括的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> includeTags;

    /**
     * 表示需要排除的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> excludeTags;

    /**
     * 表示第几页。
     * <p>构造条件时按需传入。</p>
     */
    private Integer pageNum;

    /**
     * 表示偏移量。
     * <p>构造条件时按需传入。</p>
     */
    private Integer offset;

    /**
     * 表示数量限制。
     * <p>构造条件时按需传入。</p>
     */
    private Integer limit;

    /**
     * 构造动态查询条件。
     *
     * @param toolName 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     */
    public ToolQuery(String toolName, List<String> includeTags, List<String> excludeTags, Integer pageNum,
            Integer limit) {
        this.toolName = toolName;
        this.includeTags = CollectionUtils.isNotEmpty(includeTags) ? new HashSet<>(includeTags) : new HashSet<>();
        this.excludeTags = CollectionUtils.isNotEmpty(excludeTags) ? new HashSet<>(excludeTags) : new HashSet<>();
        this.pageNum = pageNum;
        this.limit = limit;
        if (pageNum != null && limit != null) {
            this.offset = this.getOffset(pageNum, limit);
        }
    }

    private int getOffset(int pageNum, int limit) {
        return pageNum < 0 || limit < 0 ? 0 : (pageNum - 1) * limit;
    }
}

