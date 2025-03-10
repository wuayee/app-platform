/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

/**
 * 表示公共的查询类。
 *
 * @author 李金绪
 * @since 2024-09-24
 */
public class CommonQuery {
    /**
     * 表示偏移量。
     */
    private Integer offset;

    /**
     * 表示数量限制。
     */
    private Integer limit;

    /**
     * 空参构造 {@link CommonQuery}。
     */
    public CommonQuery() {}

    /**
     * 有参构造 {@link CommonQuery}。
     *
     * @param offset 表示偏移量的 {@link Integer}。
     * @param limit 表示页面大小的 {@link Integer}。
     */
    public CommonQuery(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * 获取偏移量。
     *
     * @return 表示偏移量的 {@link Integer}。
     */
    public Integer getOffset() {
        return this.offset;
    }

    /**
     * 设置偏移量。
     *
     * @param offset 表示偏移量的 {@link Integer}。
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 获取页面大小。
     *
     * @return 表示页面大小的 {@link Integer}。
     */
    public Integer getLimit() {
        return this.limit;
    }

    /**
     * 设置页面大小。
     *
     * @param limit 表示页面大小的 {@link Integer}。
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}