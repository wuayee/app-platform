/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.server.handler.MockHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link QueryFetcher } 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-20
 */
@DisplayName("测试 QueryFetcher 类")
class QueryFetcherTest {
    private final QueryFetcher queryFetcher = new QueryFetcher("name");

    @Test
    @DisplayName("判断来源数据的常用格式是否是数组")
    void shouldReturnIsArrayAble() {
        final boolean isArrayAble = this.queryFetcher.isArrayAble();
        assertThat(isArrayAble).isTrue();
    }

    @Test
    @DisplayName("从 Http 请求和响应中获取数据")
    void shouldReturnList() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final Object obj = this.queryFetcher.get(serverRequest.getRequest(), null);
        assertThat(obj).asList().hasSize(0);
    }
}
