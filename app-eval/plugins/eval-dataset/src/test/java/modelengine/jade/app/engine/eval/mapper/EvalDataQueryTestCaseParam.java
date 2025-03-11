/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.jade.app.engine.eval.mapper;

import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;

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