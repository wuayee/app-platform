/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EntityFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 EntityFetcher 类")
class EntityFetcherTest {
    @Test
    @DisplayName("从 Http 请求和响应中获取数据")
    void shouldReturnEntity() {
        MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        EntityFetcher entityFetcher = new EntityFetcher();
        Object obj = entityFetcher.get(serverRequest.getRequest(), null);
        assertThat(obj).isEqualTo(serverRequest.getEntity());
    }
}
