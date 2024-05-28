/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

/**
 * 字符串匹配算法。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class StringMatch implements EvalAlgorithm {
    /**
     * 评估方法。
     *
     * @param gt 表示标准答案的 {@link String}。
     * @param gm 表示应用生成答案的 {@link String}。
     * @return 表示评估得分的 {@link Double}。
     */
    @Override
    @Fitable(id = "StringMatch")
    public double eval(String gt, String gm) {
        if (gt == null || gm == null) {
            return 0;
        }

        return gt.trim().equals(gm.trim()) ? 1 : 0;
    }
}
