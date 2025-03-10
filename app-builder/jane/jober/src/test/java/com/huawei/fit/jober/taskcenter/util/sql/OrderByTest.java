/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OrderBy")
class OrderByTest {
    @Test
    @DisplayName("当指定了顺序时，解析到正确的 OrderBy 配置")
    void should_parse_order_by_from_input_string() {
        final String value = "desc(info.name)";
        OrderBy orderBy = OrderBy.parse(value);
        assertEquals(OrderBy.DESCENDING, orderBy.order());
        assertEquals("info.name", orderBy.property());
    }

    @Test
    @DisplayName("当没有指定顺序时，解析到正确的 OrderBy 配置")
    void should_parse_order_by_when_no_order_specified() {
        final String value = "info.name";
        OrderBy orderBy = OrderBy.parse(value);
        assertEquals(OrderBy.ASCENDING, orderBy.order());
        assertEquals("info.name", orderBy.property());
    }

    @Test
    @DisplayName("当未指定属性时，创建排序配置抛出异常")
    void should_throw_when_no_property_specified() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> OrderBy.of(null));
        assertEquals(ErrorCodes.PROPERTY_REQUIRED_TO_SORT.getErrorCode(), exception.getCode());
    }
}
