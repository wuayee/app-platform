/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.http.FailMessageContentUtils;
import com.huawei.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link FailMessageContentUtils} 的基本测试用例。
 *
 * @author 何天放 h00679269
 * @since 2024-04-16
 */
@DisplayName("测试 FailMessageContentUtils")
public class FailMessageContentUtilsTest {
    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置错误码并获取到所设定值")
    void shouldReturnCorrectCodeFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        FailMessageContentUtils.setCode(tlvs, 1);
        assertThat(FailMessageContentUtils.getCode(tlvs)).isEqualTo(1);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置错误消息并获取到所设定值")
    void shouldReturnCorrectMessageFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        FailMessageContentUtils.setMessage(tlvs, "test message");
        assertThat(FailMessageContentUtils.getMessage(tlvs)).isEqualTo("test message");
    }
}
