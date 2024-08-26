/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.server.HttpClassicServerResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link HttpClassicResponseFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 HttpClassicResponseFetcher 类")
class HttpClassicResponseFetcherTest {
    private final HttpClassicResponseFetcher responseFetcher = new HttpClassicResponseFetcher();

    @Test
    @DisplayName("从 Http 响应中获取数据")
    void shouldReturnHttpClassicServerResponse() {
        final HttpClassicServerResponse serverResponse = mock(HttpClassicServerResponse.class);
        final Object obj = this.responseFetcher.get(null, serverResponse);
        assertThat(obj).isEqualTo(serverResponse);
    }
}
