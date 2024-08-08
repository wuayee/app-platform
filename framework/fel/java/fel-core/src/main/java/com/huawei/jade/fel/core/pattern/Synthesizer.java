/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.pattern;

import com.huawei.jade.fel.core.document.Content;

/**
 * 表示检索结果合成算子。
 *
 * @param <I> 表示合成结果的泛型。
 * @author 易文渊
 * @since 2024-08-05
 */
@FunctionalInterface
public interface Synthesizer<I> extends Pattern<I, Content> {
    /**
     * 将检索的数据进行合成。
     *
     * @param input 表示检索数据的 {@link I}。
     * @return 表示合成结果的 {@link Content}。
     */
    Content synthesize(I input);

    @Override
    default Content invoke(I input) {
        return this.synthesize(input);
    }
}