/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.tokenizer.jtokkit;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.fel.core.tokenizer.Tokenizer;

import com.knuddels.jtokkit.api.EncodingType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link JtokkitTokenizer} 的测试集。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
@DisplayName("测试 JtokkitTokenizer")
public class JtokkitTokenizerTest {
    @Test
    @DisplayName("测试先编码，后解码，输入输出相同")
    void shouldEqualWhenEncodeThenDecode() {
        String text = "This is a test.";
        Tokenizer tokenizer = new JtokkitTokenizer(EncodingType.CL100K_BASE);
        List<Integer> tokens = tokenizer.encode(text);
        assertThat(tokens).isNotEmpty();
        assertThat(tokenizer.decode(tokens)).isEqualTo(text);
    }
}