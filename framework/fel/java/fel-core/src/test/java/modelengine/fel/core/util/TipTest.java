/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.util;

import static org.assertj.core.api.Assertions.assertThat;

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
}