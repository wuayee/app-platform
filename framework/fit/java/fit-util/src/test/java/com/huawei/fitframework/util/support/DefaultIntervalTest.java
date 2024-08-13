/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.model.Interval;
import com.huawei.fitframework.model.support.DefaultInterval;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultInterval} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-15
 */
public class DefaultIntervalTest {
    @Nested
    @DisplayName("Test method: toString()")
    class TestToString {
        @Test
        @DisplayName("Given allowed min 0 and allowed max 1 then return [0, 1]")
        void givenAllowedMin0AndAllowedMax1ThenReturnCorrectString() {
            Interval<Integer> interval = Interval.create(0, 1, true, true);
            String actual = interval.toString();
            assertThat(actual).isEqualTo("[0, 1]");
        }

        @Test
        @DisplayName("Given not allowed min 0 and not allowed max 1 then return (0, 1)")
        void givenNotAllowedMin0AndNotAllowedMax1ThenReturnCorrectString() {
            Interval<Integer> interval = Interval.create(0, 1, false, false);
            String actual = interval.toString();
            assertThat(actual).isEqualTo("(0, 1)");
        }
    }
}
