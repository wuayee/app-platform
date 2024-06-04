/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bleu 算法，用于评估文本相似度。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class Bleu implements EvalAlgorithm {
    // 噪音，防止n-gram计算精度为0。
    private static final double SIGMA = 0.005d;

    /**
     * 评估方法。
     *
     * @param gt 表示标准答案的 {@link String}。
     * @param gm 表示应用生成答案的 {@link String}。
     * @return 表示评估得分的 {@link Double}。
     */
    @Override
    @Fitable(id = "Bleu")
    public double eval(String gt, String gm) {
        if (gt == null || gm == null) {
            return 0;
        }

        List<String> gtDict = Analyzer.splitWithoutPunctuation(gt);
        List<String> gmDict = Analyzer.splitWithoutPunctuation(gm);

        if (gmDict.isEmpty() || gtDict.isEmpty()) {
            return 0;
        }

        return (gtDict.size() < gmDict.size() ? 1 : Math.exp(1 - (double) gtDict.size() / gmDict.size()))
                * Math.exp(
                        (Math.log(getNGramPrecision(1, gtDict, gmDict))
                                        + Math.log(getNGramPrecision(2, gtDict, gmDict))
                                        + Math.log(getNGramPrecision(3, gtDict, gmDict))
                                        + Math.log(getNGramPrecision(4, gtDict, gmDict)))
                                / 4);
    }

    /**
     * n-gram 精度算法。
     *
     * @param n 表示 n-gram 算法中参数 n 的 {@link Integer}。
     * @param gtDict 表示标准参考的分词列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param gmDict 表示应用生成的分词列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示精度得分的 {@link Float}。
     */
    public double getNGramPrecision(int n, List<String> gtDict, List<String> gmDict) {
        if (n > gtDict.size() || n > gmDict.size() || n == 0) {
            return SIGMA;
        }

        List<String> gtNgram = new ArrayList<>();
        List<String> gmNgram = new ArrayList<>();

        for (int i = 0; i <= gtDict.size() - n; i++) {
            gtNgram.add(gtDict.subList(i, i + n).stream().collect(Collectors.joining()));
        }

        for (int i = 0; i <= gmDict.size() - n; i++) {
            gmNgram.add(gmDict.subList(i, i + n).stream().collect(Collectors.joining()));
        }

        double hits = 0d;
        for (String item : gmNgram) {
            if (gtNgram.contains(item)) {
                hits++;
            }
        }

        if (hits < gmNgram.size()) {
            hits += SIGMA;
        }

        return hits / gmNgram.size();
    }
}
