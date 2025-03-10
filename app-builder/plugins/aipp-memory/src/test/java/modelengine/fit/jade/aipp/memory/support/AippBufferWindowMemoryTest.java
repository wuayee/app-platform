/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory.support;

import static modelengine.fit.jade.aipp.memory.util.TestUtils.genHistories;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.core.memory.Memory;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fit.jade.aipp.memory.AippMemoryFactory;
import modelengine.fit.jade.aipp.memory.DefaultAippMemoryFactory;
import modelengine.fit.jade.aipp.memory.util.SimpleTokenizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link AippBufferWindowMemory} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public class AippBufferWindowMemoryTest {
    private final AippMemoryFactory factory = new DefaultAippMemoryFactory(new SimpleTokenizer());

    @Test
    @DisplayName("若当前缓存数小于最大缓存数，返回全部历史记录")
    void shouldOkWhenBufferEnough() {
        AippMemoryConfig config = new AippMemoryConfig();
        config.setWindowAlg("buffer_window");
        config.setSerializeAlg("full");
        config.setProperty(5);
        Memory memory = factory.create(config, genHistories(3));
        assertThat(memory).isInstanceOf(AippBufferWindowMemory.class)
                .extracting(Memory::text)
                .isEqualTo("Q: 0\nA: 0\nQ: 1\nA: 1\nQ: 2\nA: 2");
    }

    @Test
    @DisplayName("若当前缓存数大于最大缓存数，返回最近的最大缓存数大小的历史记录")
    void shouldOkWhenBufferNotEnough() {
        AippMemoryConfig config = new AippMemoryConfig();
        config.setWindowAlg("buffer_window");
        config.setSerializeAlg("full");
        config.setProperty(2);
        Memory memory = factory.create(config, genHistories(3));
        assertThat(memory).isInstanceOf(AippBufferWindowMemory.class)
                .extracting(Memory::text)
                .isEqualTo("Q: 1\nA: 1\nQ: 2\nA: 2");
    }
}