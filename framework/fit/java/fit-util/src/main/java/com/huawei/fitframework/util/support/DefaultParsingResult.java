/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import com.huawei.fitframework.util.ParsingResult;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link ParsingResult} 提供默认实现。
 *
 * @param <T> 表示结果的类型。
 * @author 梁济时 l00815032
 * @since 1.0
 */
public class DefaultParsingResult<T> implements ParsingResult<T> {
    private boolean parsed;
    private T result;

    /**
     * 使用表示是否解析成功的值及解析的结果初始化 {@link DefaultParsingResult} 类的新实例。
     *
     * @param parsed 若解析成功，则为 {@code true}；否则为 {@code false}。
     * @param result 表示解析的结果。
     */
    public DefaultParsingResult(boolean parsed, T result) {
        this.parsed = parsed;
        this.result = result;
    }

    @Override
    public boolean isParsed() {
        return this.parsed;
    }

    @Override
    public T getResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return StringUtils.format("[parsed={0}, result={1}]", this.isParsed(), this.getResult());
    }
}
