/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema;

import lombok.Data;

import java.util.List;

/**
 * 表示评估数据校验测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
@Data
public class SchemaValidatorImplTestCaseParam {
    private String schema;
    private List<String> content;
}
