/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultSecureAccess} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@DisplayName("测试 DefaultSecureAccess")
public class DefaultSecureAccessTest {
    @Test
    @DisplayName("测试正确创建实例")
    void shouldCreateInstance() {
        DefaultSecureAccess secureAccess = new DefaultSecureAccess();
        secureAccess.setAccessKey("AK");
        secureAccess.setSecretKey("SK");
        secureAccess.setEnabled(true);
        secureAccess.setEncrypted(false);
        Assertions.assertEquals("AK", secureAccess.accessKey());
        Assertions.assertEquals("SK", secureAccess.secretKey());
        Assertions.assertTrue(secureAccess.enabled());
        Assertions.assertFalse(secureAccess.encrypted());
    }
}
