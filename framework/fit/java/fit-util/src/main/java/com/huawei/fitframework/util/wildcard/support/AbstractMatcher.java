/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.wildcard.Matcher;
import com.huawei.fitframework.util.wildcard.Pattern;
import com.huawei.fitframework.util.wildcard.Result;
import com.huawei.fitframework.util.wildcard.SymbolType;

/**
 * 为匹配程序提供基类。
 *
 * @param <T> 表示所匹配的内容中元素的类型。
 * @author 梁济时 l00815032
 * @since 2022-07-28
 */
public abstract class AbstractMatcher<T> implements Matcher<T> {
    /**
     * 获取正在匹配的模式。
     *
     * @return 表示正在匹配的模式的 {@link Pattern}。
     */
    protected abstract Pattern<T> pattern();

    /**
     * 获取上一个结果。
     *
     * @return 表示上一个结果的  {@link Result}。
     */
    protected abstract Result<T> previous();

    @Override
    public Result<T> match(T value) {
        MatchingResult<T> result = new MatchingResult<>(this.pattern());
        for (int i = 0; i < this.pattern().length(); i++) {
            T symbol = this.pattern().at(i);
            SymbolType type = nullIf(this.pattern().symbols().classifier().classify(symbol), SymbolType.NORMAL);
            switch (type) {
                case NORMAL:
                    if (this.pattern().symbols().matcher().match(symbol, value)) {
                        result.set(i, this.previous().get(i - 1));
                    }
                    break;
                case SINGLE_WILDCARD:
                    result.set(i, this.previous().get(i - 1));
                    break;
                case MULTIPLE_WILDCARD:
                    result.set(i, result.get(i - 1) || this.previous().get(i));
                    break;
                default:
                    throw new IllegalStateException(StringUtils.format(
                            "Unknown symbol type. [symbol={0}, type={1}]", symbol, type));
            }
        }
        return result;
    }
}
