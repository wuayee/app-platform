/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fit.http.server.handler.MockHttpClassicServerRequest.URI_KEY;
import static com.huawei.fit.http.server.handler.MockHttpClassicServerRequest.URI_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.server.handler.MockHttpClassicServerRequest;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link PriorityPropertyValueMapper} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-02-21
 */
@DisplayName("测试 PriorityParameterMapper 类")
class PriorityPropertyValueMapperTest {
    @Test
    @DisplayName("选取第一个成功获取的结果")
    void shouldReturnFirstResult() {
        final QueryFetcher queryFetcher1 = new QueryFetcher(URI_KEY);
        final QueryFetcher queryFetcher2 = new QueryFetcher("notExistKey");
        UniqueSourcePropertyValueMapper mapper1 =
                new UniqueSourcePropertyValueMapper(queryFetcher1, false);
        UniqueSourcePropertyValueMapper mapper2 =
                new UniqueSourcePropertyValueMapper(queryFetcher2, false);
        final List<PropertyValueMapper> mappers = Arrays.asList(mapper1, mapper2);
        final PropertyValueMapper propertyValueMapper = new PriorityPropertyValueMapper(mappers);
        final DefaultHttpClassicServerRequest request = new MockHttpClassicServerRequest().getRequest();
        final Object value = propertyValueMapper.map(request, null);
        assertThat(value).isEqualTo(URI_VALUE);
    }
}
