/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.http.WebsocketUtils;
import com.huawei.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link WebsocketUtils} 的基本测试用例。
 *
 * @author 何天放 h00679269
 * @since 2024-04-16
 */
@DisplayName("测试 WebsocketUtils")
public class WebsocketUtilsTest {
    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置索引并获取到所设定值")
    void shouldReturnCorrectIndexFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        WebsocketUtils.setIndex(tlvs, 1);
        assertThat(WebsocketUtils.getIndex(tlvs)).isEqualTo(1);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置类型信息并获取到所设定值")
    void shouldReturnCorrectTypeFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        WebsocketUtils.setType(tlvs, 1);
        assertThat(WebsocketUtils.getType(tlvs)).isEqualTo(1);
    }

    @Test
    @DisplayName("能够向 TagLengthValues 中正确设置内容并获取到所设定值")
    void shouldReturnCorrectContentFromTagLengthValues() {
        TagLengthValues tlvs = TagLengthValues.create();
        byte[] byteArray = {1, 2, 3, 4, 5};
        WebsocketUtils.setContent(tlvs, byteArray);
        assertThat(WebsocketUtils.getContent(tlvs)).isEqualTo(byteArray);
    }
}
