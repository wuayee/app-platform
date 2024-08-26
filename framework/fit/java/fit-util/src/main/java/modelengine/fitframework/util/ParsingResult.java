/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package modelengine.fitframework.util;

import static modelengine.fitframework.util.ObjectUtils.cast;

/**
 * 为解析字符串提供结果。
 *
 * @param <T> 表示解析结果的类型的 {@link T}。
 * @author 梁济时
 * @since 1.0
 */
public interface ParsingResult<T> {
    /**
     * 获取用以表示解析失败的结果。
     *
     * @param <T> 表示结果的类型。
     * @return 表示解析失败的结果的 {@link ParsingResult}。
     */
    static <T> ParsingResult<T> failed() {
        return ObjectUtils.cast(ParsingResultUtils.FAILED);
    }

    /**
     * 获取一个值，该值指示是否解析成功。
     *
     * @return 若解析成功，则为 {@code true}；否则为 {@code false}。
     */
    boolean isParsed();

    /**
     * 表示解析得到的结果。
     *
     * @return 表示解析结果。
     */
    T getResult();
}
