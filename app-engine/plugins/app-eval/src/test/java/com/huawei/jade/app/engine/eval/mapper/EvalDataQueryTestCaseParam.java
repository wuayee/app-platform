/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;

import lombok.Data;

/**
 * 表示数据集查询测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDataQueryTestCaseParam {
    private EvalDataQueryParam queryParam;
    private int expectedSize;
    private String expectedContent;
}
