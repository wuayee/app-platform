/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import modelengine.fit.jober.taskcenter.service.PluginLoginService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link PluginLoginServiceImpl} 对应测试类。
 *
 * @author 姚江
 * @since 2024-02-18
 */
@ExtendWith(MockitoExtension.class)
public class PluginLoginServiceImplTest {
    @Mock
    private DynamicSqlExecutor executor;

    private PluginLoginService service;

    @BeforeEach
    public void beforeEach() {
        this.service = new PluginLoginServiceImpl(executor);
    }

    @Nested
    @DisplayName("测试delete方法")
    class TestDelete {
        @Test
        @DisplayName("测试通过")
        public void test() {
            Assertions.assertDoesNotThrow(() -> service.delete("1234567890123456"));
        }
    }

    @Nested
    @DisplayName("测试save方法")
    class TestSave {
        @Test
        @DisplayName("测试通过")
        public void test() {
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(1);

            Assertions.assertDoesNotThrow(() -> service.save("1234567890123456", "cookie"));
        }
    }

    @Nested
    @DisplayName("测试get方法")
    class TestGet {
        @Test
        @DisplayName("测试通过")
        public void test() {
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn("cookie");
            String cookie = service.get("1234567890123456");

            Assertions.assertEquals("cookie", cookie);
        }
    }
}
