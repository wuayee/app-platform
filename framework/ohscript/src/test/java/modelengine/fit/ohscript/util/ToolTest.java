/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * 测试工具方法
 *
 * @author songyongtan
 * @since 2024/9/27
 */
class ToolTest extends Tool {
    @Test
    void shouldReturnPositiveNumberWhenNewIdGivenIdWillOverFlow() {
        setId(Long.MAX_VALUE);
        assertEquals(2, Tool.newId());

        setId(-1);
        assertEquals(2, Tool.newId());
    }

    @Test
    void shouldIncrementWhenNewIdGivenPositiveNumber() {
        setId(10);
        assertEquals(11, Tool.newId());
        assertEquals(12, Tool.newId());
    }
}