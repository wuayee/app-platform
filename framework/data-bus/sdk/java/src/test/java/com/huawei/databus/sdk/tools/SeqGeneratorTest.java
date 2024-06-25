/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link SeqGenerator} 的单元测试。
 *
 * @author 王成 w00863339
 * @since 2024-06-22
 */
@DisplayName("测试 SeqGenerator 类")
class SeqGeneratorTest {
    @BeforeEach
    private void setUp() {
        SeqGenerator.getInstance().setCurrentNumber(1L);
    }

    @Test
    @DisplayName("测试单例是否相等")
    public void testSingletonBehavior() {
        SeqGenerator.getInstance().setCurrentNumber(1L);
        SeqGenerator instance1 = SeqGenerator.getInstance();
        SeqGenerator instance2 = SeqGenerator.getInstance();
        Assertions.assertSame(instance1, instance2, "The instances should be the same.");
    }

    @Test
    @DisplayName("测试连续获取号码是否递增")
    public void testNumberGeneration() {
        SeqGenerator generator = SeqGenerator.getInstance();

        long firstNumber = generator.getNextNumber();
        long secondNumber = generator.getNextNumber();
        Assertions.assertEquals(1, firstNumber, "The first number should be 1.");
        Assertions.assertEquals(2, secondNumber, "The second number should be 2.");
    }

    @Test
    @DisplayName("测试号码到达最大值时是否正确回绕")
    public void testNumberLoopback() {
        SeqGenerator generator = SeqGenerator.getInstance();
        generator.setCurrentNumber(Integer.MAX_VALUE);

        // 检查复位行为
        long resetNumber = generator.getNextNumber();
        Assertions.assertEquals(1, resetNumber, "The number should reset to 1 after reaching Integer.MAX_VALUE.");
    }
}