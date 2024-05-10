/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

/**
 * 重排序模块的接口定义。
 *
 * @param <D> 表示要进行重排序数据的 {@link D}。
 * @param <O> 表示重排序后的数据的 {@link O}。
 * @since 2024-05-07
 */
public interface Rerank <D, O> {
    /**
     * 对检索到的数据进行重排序。
     *
     * @param data data 表示经过索引检索到的数据的 {@link O}。
     * @return 回重排序后的数据。
     */
    O invoke(D data);

    /**
     * 根据query内容对检索到的数据进行重排序。
     *
     * @param query 表示来自检索器的查询字段的 {@link String}。
     * @param data 表示经过索引检索到的数据的 {@link D}。
     * @return 返回重排序后的数据。
     */
    O invoke(String query, D data);
}
