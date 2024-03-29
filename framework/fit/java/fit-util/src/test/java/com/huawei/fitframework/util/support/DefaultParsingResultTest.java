/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * 为 {@link DefaultParsingResult} 提供单元测试。
 *
 * @author 梁济时 l00815032
 * @since 1.0
 */
public class DefaultParsingResultTest {
    /** 表示用以测试的对象实例。 */
    private static final DefaultParsingResult<Integer> RESULT = new DefaultParsingResult<>(true, 1);

    /** 表示用以测试的对象实例的字符串表现形式。 */
    private static final String RESULT_STRING = "[parsed=true, result=1]";

    /**
     * 目标方法：{@link DefaultParsingResult#toString()}
     * <p>{@link DefaultParsingResult#toString() toString()} 方法返回正确的字符串表现。</p>
     */
    @Test
    public void should_return_correct_string_representation() {
        assertEquals(RESULT_STRING, RESULT.toString());
    }
}
