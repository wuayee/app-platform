/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.vectorstore;

import com.huawei.fitframework.pattern.builder.BuilderFactory;
import com.huawei.jade.fel.core.retriever.filter.Filter;

/**
 * 表示向量检索的参数。
 *
 * @author 易文渊
 * @since 2024-08-06
 */
public interface SearchOption {
    /**
     * 获取检索返回数量。
     *
     * @return 表示检索返回数量的 {@code int}。
     */
    int topK();

    /**
     * 获取标量过滤器。
     *
     * @return 表示标量过滤器的 {@link Filter}。
     */
    Filter filter();

    /**
     * 表示 {@link SearchOption} 的构建器。
     */
    interface Builder {
        /**
         * 设置检索返回数量。
         *
         * @param topK 表示检索返回数量的 {@code int}。
         * @return 表示当前构建器实例的 {@link Builder}。
         */
        Builder topK(int topK);

        /**
         * 设置标量过滤器。
         *
         * @param filter 表示标量过滤器的 {@link Filter}。
         * @return 表示当前构建器实例的 {@link Builder}。
         */
        Builder filter(Filter filter);

        /**
         * 构建 {@link SearchOption} 的实例。
         *
         * @return 表示构建实例的 {@link SearchOption}。
         */
        SearchOption build();
    }

    /**
     * 获取 {@link SearchOption} 的构建器。
     *
     * @return 表示创建成功构建器的 {@link Builder}。
     */
    static Builder custom() {
        return BuilderFactory.get(SearchOption.class, SearchOption.Builder.class).create(null);
    }
}