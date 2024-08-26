/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static modelengine.fit.http.server.handler.MockHttpClassicServerRequest.HEADER_KEY;
import static modelengine.fit.http.server.handler.MockHttpClassicServerRequest.HEADER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link UniqueSourcePropertyValueMapper } 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-20
 */
@DisplayName("测试 UniqueSourceParameterMapper 类")
class UniqueSourcePropertyValueMapperTest {
    @Test
    @DisplayName("将 Http 请求和响应通过规则映射成为一个指定值")
    void givenMapWhenDestinationNameIsNullThenReturnString() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final DefaultHttpClassicServerRequest request = serverRequest.getRequest();
        UniqueSourcePropertyValueMapper mapper =
                new UniqueSourcePropertyValueMapper(new HeaderFetcher(HEADER_KEY), false);
        final Object value = mapper.map(request, null, null);
        assertThat(value).isEqualTo(HEADER_VALUE);
    }
}
