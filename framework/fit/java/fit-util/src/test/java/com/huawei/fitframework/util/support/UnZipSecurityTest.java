/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link Unzip.Security} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-02
 */
public class UnZipSecurityTest {
    @Nested
    @DisplayName("test method: toString()")
    class TestToString {
        @Test
        @DisplayName("Given default security then return readable message")
        void givenDefaultSecurityThenReturnReadableMessage() {
            Unzip.Security security = Unzip.Security.DEFAULT;
            String actual = security.toString();
            assertThat(actual).isEqualTo("[compressedTotalSize=104857600, entryMaxCount=1024]");
        }
    }
}
