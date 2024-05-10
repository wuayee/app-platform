/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试索引的校验逻辑")
class IndexValidatorImplTest {
    private static final int NAME_LENGTH_MAXIMUM = 3;

    private IndexValidatorImpl validator;

    @BeforeEach
    void setup() {
        this.validator = new IndexValidatorImpl(NAME_LENGTH_MAXIMUM);
    }

    @Test
    @DisplayName("当索引名称是空白字符串时，抛出异常")
    void should_throw_when_name_is_blank() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.name("     "));
        assertEquals(ErrorCodes.INDEX_NAME_REQUIRED.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当索引名称的长度超出限制时，抛出异常")
    void should_throw_when_name_length_out_of_bounds() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.validator.name("1234"));
        assertEquals(ErrorCodes.INDEX_NAME_LENGTH_OUT_OF_BOUNDS.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当索引名称符合要求时，返回标准化后的名称")
    void should_return_valid_index_name() {
        String name = this.validator.name(" a c ");
        assertEquals("a c", name);
    }
}