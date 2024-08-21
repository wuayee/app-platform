/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package modelengine.fitframework.util.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fitframework.model.support.DefaultRangeResult;

import org.junit.jupiter.api.Test;

/**
 * 为 {@link DefaultRangeResult} 提供单元测试。
 *
 * @author 梁济时
 * @since 1.0
 */
public class DefaultRangeResultTest {
    /** 表示用以测试的对象实例。 */
    private static final DefaultRangeResult RESULT = new DefaultRangeResult(400, 200, 1000);

    /** 表示用以测试的对象实例的字符串表现形式。 */
    private static final String RESULT_STRING = "[offset=400, limit=200, total=1000]";

    /**
     * 目标方法：{@link DefaultRangeResult#toString()}
     * <p>{@link DefaultRangeResult#toString() toString()} 方法返回正确的字符串表现。</p>
     */
    @Test
    public void should_return_correct_string_representation() {
        assertEquals(RESULT_STRING, RESULT.toString());
    }
}
