/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.document;

import com.huawei.jade.fel.core.pattern.PostProcessor;

import java.util.List;

/**
 * 表示检索文档的后置处理器算子接口。
 *
 * @author 易文渊
 * @since 2024-08-06
 */
@FunctionalInterface
public interface DocumentPostProcessor extends PostProcessor<List<MeasurableDocument>> {
    /**
     * 对检索结果进行后处理。
     *
     * @param documents 表示输入文档的 {@link List}{@code <}{@link MeasurableDocument}{@code >}。
     * @return 表示处理后文档的 {@link List}{@code <}{@link MeasurableDocument}{@code >}。
     */
    List<MeasurableDocument> process(List<MeasurableDocument> documents);
}