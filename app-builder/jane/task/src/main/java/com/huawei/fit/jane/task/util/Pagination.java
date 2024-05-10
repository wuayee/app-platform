/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.util;

/**
 * 表示分页信息。
 *
 * @author 梁济时 l00815032
 * @since 2023-12-12
 */
public interface Pagination {
    /**
     * 获取待查询的结果集在全量结果集中的偏移量。
     *
     * @return 表示偏移量的 64 位整数。
     */
    long offset();

    /**
     * 获取待查询的结果集中包含数据记录的最大数量。
     *
     * @return 表示分页结果集中数据记录最大数量的 32 位整数。
     */
    int limit();

    /**
     * 创建分页信息。
     *
     * @param offset 表示偏移量的 64 位整数。
     * @param limit 表示分页结果集中数据记录最大数量的 32 位整数。
     * @return 表示新创建的分页信息的 {@link Pagination}。
     */
    static Pagination create(long offset, int limit) {
        return new DefaultPagination(offset, limit);
    }
}
