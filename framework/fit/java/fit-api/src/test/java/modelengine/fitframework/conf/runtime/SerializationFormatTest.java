/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.conf.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 表示 {@link SerializationFormat} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-04
 */
@DisplayName("测试 SerializationFormat")
public class SerializationFormatTest {
    @ParameterizedTest
    @DisplayName("给定序列化方式的编码，可以正确的解析为序列化方式的枚举类型")
    @CsvSource({
            "-1,UNKNOWN", "0,PROTOBUF", "1,JSON", "2,CBOR", "-500,UNKNOWN"
    })
    void shouldReturnCorrectFormatGivenCode(int code, SerializationFormat expected) {
        SerializationFormat actual = SerializationFormat.from(code);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("给定序列化方式的名字，可以正确的解析为序列化方式的枚举类型")
    @CsvSource({
            "unknown,UNKNOWN", "protobuf,PROTOBUF", "json,JSON", "cbor,CBOR", "error,UNKNOWN"
    })
    void shouldReturnCorrectFormatGivenName(String name, SerializationFormat expected) {
        SerializationFormat actual = SerializationFormat.from(name);
        assertThat(actual).isEqualTo(expected);
    }
}
