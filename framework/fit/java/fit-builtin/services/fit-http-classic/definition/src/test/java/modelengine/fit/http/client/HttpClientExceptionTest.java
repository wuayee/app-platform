/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fit.http.client;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.client.HttpClientException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link HttpClientException} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 HttpClientException 类")
public class HttpClientExceptionTest {
    @Test
    @DisplayName("给定一个 String 参数，实例化对象成功")
    void givenStringParameterThenInitializeObjectSuccessfully() {
        String message = "testMessage";
        HttpClientException httpClientException = new HttpClientException(message);
        assertThat(httpClientException.getMessage()).isEqualTo(message);
    }
}
