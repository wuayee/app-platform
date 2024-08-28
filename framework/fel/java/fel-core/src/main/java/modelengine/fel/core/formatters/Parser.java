/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.formatters;

import modelengine.fel.core.Pattern;

/**
 * 表示字符串解析接口，将字符串转换成指定对象。
 *
 * @param <I> 表示输入对象类型。
 * @param <R> 表示输出对象类型。
 * @author 易文渊
 * @since 2024-04-28
 */
@FunctionalInterface
public interface Parser<I, R> extends Pattern<I, R> {
    /**
     * 将字符串转换为对象。
     *
     * @param input 表示输入的 {@link I}。
     * @return 表示输出对象的 {@link R}。
     */
    R parse(I input);

    @Override
    default R invoke(I input) {
        return this.parse(input);
    }
}