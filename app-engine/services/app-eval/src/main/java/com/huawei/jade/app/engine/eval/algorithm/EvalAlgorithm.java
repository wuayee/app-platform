/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 评估算法接口
 *
 * @since 2024/05/28
 */
public interface EvalAlgorithm {
    /**
     * 评估方法入口
     *
     * @param gt 标准答案
     * @param gm 应用生成答案
     * @return 评估得分
     */
    @Genericable(id = "com.huawei.jade.app.engine.eval.algorithm.eval")
    double eval(String gt, String gm);
}
