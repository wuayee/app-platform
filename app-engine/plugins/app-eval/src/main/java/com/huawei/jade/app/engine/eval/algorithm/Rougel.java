/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.util.List;

/**
 *  Rougel 基于最长公共子序列的文本相似度评估算法。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class Rougel implements EvalAlgorithm {
    /**
     * 评估方法。
     *
     * @param gt 表示标准答案的 {@link String}。
     * @param gm 表示应用生成答案的 {@link String}。
     * @return 表示评估得分的 {@link Double}。
     */
    @Override
    @Fitable(id = "RougeL")
    public double eval(String gt, String gm) {
        if (gt == null || gm == null) {
            return 0;
        }

        List<String> gtDict = Analyzer.splitWithoutPunctuation(gt);
        List<String> gmDict = Analyzer.splitWithoutPunctuation(gm);

        return getRougeL(getLcsLen(gtDict, gmDict), gtDict.size(), gmDict.size());
    }

    /**
     * 获取最长公共子序列。
     *
     * @param gtDict 表示标准参考的分词列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param gmDict 表示应用生成的分词列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示最长公共子序列的长度的 {@link Integer}
     */
    private int getLcsLen(List<String> gtDict, List<String> gmDict) {
        int[][] dp = new int[gtDict.size() + 1][gmDict.size() + 1];
        for (int i = 1; i <= gtDict.size(); i++) {
            for (int j = 1; j <= gmDict.size(); j++) {
                if (gtDict.get(i - 1).equals(gmDict.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[gtDict.size()][gmDict.size()];
    }

    /**
     * 计算 RougeL 精度。
     *
     * @param lcsLen 表示最长公共子序列长度 {@link Integer}。
     * @param gtLen 表示应用生成的分词列表长度的 {@link Integer}。
     * @param gmLen 表示应用生成的分词列表长度的 {@link Integer}。
     * @return 表示RougeL精度的 {@link Double}
     */
    private double getRougeL(int lcsLen, int gtLen, int gmLen) {
        double precision = (double) lcsLen / (double) gmLen;
        double recall = (double) lcsLen / (double) gtLen;
        return 2 * precision * recall / (precision + recall);
    }
}
