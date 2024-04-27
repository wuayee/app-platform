/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link Tip} 的单元测试。
 *
 * @author 易文渊
 * @since 2024-04-27
 */
@DisplayName("测试 Tip")
public class TipTest {
    @Test
    @DisplayName("当tip执行合并操作时，返回正确结果")
    void giveAnotherTipThenMergeOk() {
        Tip tip = Tip.from("k1", "v1").merge(Tip.from("k2", "v2"));
        assertThat(tip.freeze()).containsKeys("k1", "k2");
    }


    @Test
    @DisplayName("当tip冻结后，执行任意操作抛出异常")
    void FreezeThenOperateShouldThrowException() {
        Tip tip = new Tip();
        assertThat(tip.freeze()).isEmpty();
        assertThatThrownBy(() -> tip.add("k")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> tip.merge(new Tip())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(tip::freeze).isInstanceOf(IllegalStateException.class);
    }
}