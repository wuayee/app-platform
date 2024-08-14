/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema;

import lombok.Data;

/**
 * 表示评估数据约束生成测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-08-07
 */
@Data
public class SchemaGeneratorImplTestCaseParam {
    private String json;
    private String schema;
}
