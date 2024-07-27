/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * 表示操作单元导出器的容器。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
public interface SpanExporterContainer {
    /**
     * 注册操作单元导出器。
     *
     * @param exporter 表示操作单元导出器的 {@link CarverSpanExporter}。
     */
    void register(CarverSpanExporter exporter);

    /**
     * 注销操作单元导出器。
     *
     * @param exporter 表示操作单元导出器的 {@link CarverSpanExporter}。
     */
    void unregister(CarverSpanExporter exporter);

    /**
     * 条件查询操作单元导出器。
     *
     * @param predicate 表示过滤器的 {@link Predicate}{@code <}{@link CarverSpanExporter}{@code >}。
     * @return 表示操作单元导出器列表的 {@link List}{@code <}{@link CarverSpanExporter}{@code >}。
     */
    List<CarverSpanExporter> get(Predicate<CarverSpanExporter> predicate);
}
