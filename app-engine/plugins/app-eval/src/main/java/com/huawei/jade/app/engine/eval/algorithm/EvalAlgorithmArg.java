/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评估算法参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalAlgorithmArg {
    private String gt;
    private String gm;
}
