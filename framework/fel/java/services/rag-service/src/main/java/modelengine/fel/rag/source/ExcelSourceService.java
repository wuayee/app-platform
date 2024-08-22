/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.source;

import modelengine.fitframework.annotation.Genericable;

/**
 * Excel数据源处理服务
 *
 * @since 2024-06-04
 */
public interface ExcelSourceService {
    /**
     * 解析提取excel中的表头及内容信息,用于在aiflow中作为数据源输入触发流程。
     *
     * @param options 表示读取相关参数 {@link ExcelSourceOptions}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.source.excel.load")
    void load(ExcelSourceOptions options);

    /**
     * 解析提取excel中的表头及内容信息。
     *
     * @param options 表示读取相关参数 {@link ExcelSourceOptions}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.source.excel.parse")
    void parseContent(ExcelSourceOptions options);
}
