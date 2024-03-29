/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.server.handler.MockHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link CookieFetcher} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-02-15
 */
@DisplayName("测试 CookieFetcher 类")
class CookieFetcherTest {
    @Test
    @DisplayName("当请求和响应中没有 cookie 名称时，返回 null")
    void givenEntityImplThenReturnParameterMapper() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final CookieFetcher cookieFetcher = new CookieFetcher("notExistCookieName");
        final Object obj = cookieFetcher.get(serverRequest.getRequest(), null);
        assertThat(obj).isNull();
    }

    @Test
    @DisplayName("判断来源数据的常用格式是否是数组")
    void shouldReturnIsArrayAble() {
        final CookieFetcher cookieFetcher = new CookieFetcher("notExistCookieName");
        final boolean isArrayAble = cookieFetcher.isArrayAble();
        assertThat(isArrayAble).isFalse();
    }
}
