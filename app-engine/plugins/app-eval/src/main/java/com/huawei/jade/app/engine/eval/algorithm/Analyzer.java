/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分词器。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
public class Analyzer {
    /**
     * 忽略标点符号的分词方法。
     *
     * @param input 表示待分词字符串的 {@link String}。
     * @return 表示分割好的字符串数组的 {@link List}{@code <}{@link String}{@code >}。
     */
    public static List<String> splitWithoutPunctuation(String input) {
        List<Term> termList = HanLP.segment(input);
        return termList.stream().filter(t -> !t.nature.startsWith('w')).map(t -> t.word).collect(Collectors.toList());
    }
}
