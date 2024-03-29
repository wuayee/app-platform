/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试字符序适配器")
class SymbolSequenceCharSequenceAdapterTest {
    private SymbolSequence<Character> sequence;

    @BeforeEach
    void setup() {
        this.sequence = SymbolSequence.fromCharSequence("abc");
    }

    @Test
    @DisplayName("返回的长度正确")
    void should_return_correct_length() {
        assertEquals(3, this.sequence.length());
    }

    @Test
    @DisplayName("返回正确的数据")
    void should_return_correct_data() {
        assertEquals('a', this.sequence.at(0));
        assertEquals('b', this.sequence.at(1));
        assertEquals('c', this.sequence.at(2));
    }

    @Test
    @DisplayName("返回友好的字符串表现形式")
    void should_return_friendly_string() {
        assertEquals("abc", this.sequence.toString());
    }
}
