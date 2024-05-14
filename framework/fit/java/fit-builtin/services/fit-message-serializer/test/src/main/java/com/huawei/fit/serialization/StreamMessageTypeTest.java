/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.http.websocket.StreamMessageType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * {@link StreamMessageType} 的基本测试用例。
 *
 * @author 何天放 h00679269
 * @since 2024-04-16
 */
@DisplayName("测试 StreamMessageType")
public class StreamMessageTypeTest {
    @ParameterizedTest
    @DisplayName("通过名字转换为枚举时，转换的方式正确")
    @CsvSource({
            "UNKNOWN, UNKNOWN", "Consume, CONSUME", "complete, COMPLETE", "fail, FAIL", "ReQuEsT, REQUEST",
            "CANCEL, CANCEL", ", UNKNOWN", "ABC, UNKNOWN"
    })
    void shouldReturnCorrectEnumAfterConvertFromName(String name, StreamMessageType expected) {
        StreamMessageType actual = StreamMessageType.fromName(name);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("通过编码转换为枚举时，转换的方式正确")
    @CsvSource({
            "-1, UNKNOWN", "0, CONSUME", "1, COMPLETE", "2, FAIL", "10, REQUEST", "11, CANCEL", "100, UNKNOWN"
    })
    void shouldReturnCorrectEnumAfterConvertFromCode(int code, StreamMessageType expected) {
        StreamMessageType type = StreamMessageType.fromCode(code);
        assertThat(type).isEqualTo(expected);
    }
}
