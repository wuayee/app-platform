/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;
import modelengine.fitframework.value.ValueFetcher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link ObjectEntityFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 ObjectEntityFetcher 类")
class ObjectEntityFetcherTest {
    private final ObjectEntityFetcher objectEntityFetcher = new ObjectEntityFetcher("/a");

    @Test
    @DisplayName("从 Http 请求中获取数据")
    void shouldReturnValue() {
        final ObjectEntity<?> entity = mock(ObjectEntity.class);
        final DefaultHttpClassicServerRequest request = mock(DefaultHttpClassicServerRequest.class);
        final HttpResource httpResource = mock(HttpResource.class);
        final ValueFetcher valueFetcher = mock(ValueFetcher.class);
        when(request.httpResource()).thenReturn(httpResource);
        when(httpResource.valueFetcher()).thenReturn(valueFetcher);
        when(valueFetcher.fetch(any(), any())).thenReturn("value");
        final Object obj = this.objectEntityFetcher.getFromRequest(request, entity);
        assertThat(obj).isEqualTo("value");
    }

    @Test
    @DisplayName("获取当前消息体获取器可以获取的消息体的类型")
    void shouldReturnEntityType() {
        final Class<? extends Entity> entityType = this.objectEntityFetcher.entityType();
        assertThat(entityType).isEqualTo(ObjectEntity.class);
    }
}
