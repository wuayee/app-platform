/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface;

import lombok.Data;

/**
 * 表示 pipline 测试用例。
 *
 * @author 易文渊
 * @since 2024-06-07
 */
@Data
public class PipelineTestCase {
    private String task;
    private String model;
    private Object input;
    private Object output;
}