/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jober.aipp.util.RandomPathUtils;

import org.junit.jupiter.api.Test;

/**
 * {@link RandomPathUtils} 的单元测试
 *
 * @author lizhichao 00829434
 * @since 2024-11-28
 */
class RandomPathUtilsTest {
    @Test
    void testValidatePath_NullPath() {
        String path = null;
        int length = 10;
        assertFalse(RandomPathUtils.validatePath(path, length));
    }

    @Test
    void testValidatePath_InvalidLength() {
        String path = "short";
        int length = 10;
        assertFalse(RandomPathUtils.validatePath(path, length));
    }

    @Test
    void testValidatePath_InvalidCharacter() {
        String path = "valid123!@#1";
        int length = 12;
        assertFalse(RandomPathUtils.validatePath(path, length));
    }

    @Test
    void testValidatePath_ValidPath() {
        String path = "YGHmQFJE5ZaFW4wl";
        int length = 16;
        assertTrue(RandomPathUtils.validatePath(path, length));
    }

    // 测试 generateRandomString 方法
    @Test
    void testGenerateRandomString_Length() {
        int length = 16;
        String result = RandomPathUtils.generateRandomString(length);
        assertEquals(length, result.length());
    }

    @Test
    void testGenerateRandomString_ValidCharacters() {
        int length = 16;
        String result = RandomPathUtils.generateRandomString(length);
        for (char c : result.toCharArray()) {
            assertTrue("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".indexOf(c) != -1);
        }
    }
}

