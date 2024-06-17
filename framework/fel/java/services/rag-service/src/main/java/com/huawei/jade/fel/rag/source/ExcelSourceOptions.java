/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Excel数据源处理服务相关参数
 *
 * @since 2024-06-04
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSourceOptions {
    private String path;
    private Integer headRow;
    private Integer dataRow;
    private Integer sheetId;
    private Integer rowNum;
}
