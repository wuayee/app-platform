/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link DigitUtils} 提供单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-02
 */
@DisplayName("测试 NumberUtils")
class NumberUtilsTest {
    @Test
    @DisplayName("当提供长整型数据时，返回该数的二进制形式，同时补充前置的若干个 '0'")
    void givenLongValueThenReturnBinary() {
        final String binary = DigitUtils.toBinary(20L);
        assertThat(binary).startsWith("0").endsWith(Long.toBinaryString(20L));
    }
}
