/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultSecure} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@DisplayName("测试 DefaultClientSecure")
public class DefaultSecureTest {
    @Test
    @DisplayName("测试正确创建实例")
    void shouldCreateInstance() {
        DefaultSecure secure = new DefaultSecure();
        secure.setSecureRandomEnabled(true);
        secure.setSecureProtocol("TLSv1.2");
        Assertions.assertTrue(secure.secureRandomEnabled());
        Assertions.assertEquals("TLSv1.2", secure.secureProtocol().get());
    }
}