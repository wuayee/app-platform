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
 * 表示 {@link AippTokenWindowMemory} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2024-09-20
 */
public class AippTokenWindowMemoryTest {
    private final AippMemoryFactory factory = new DefaultAippMemoryFactory(new SimpleTokenizer());

    @Test
    @DisplayName("若分词数小于等于最大分词数，返回全部历史记录")
    void shouldOkWhenTokenCountInsideLimit() {
        AippMemoryConfig config = new AippMemoryConfig();
        config.setWindowAlg("token_window");
        config.setSerializeAlg("full");
        config.setProperty(6);
        Memory memory = factory.create(config, genHistories(3));
        assertThat(memory).isInstanceOf(AippTokenWindowMemory.class);
        assertThat(memory.messages().size()).isEqualTo(6);
    }

    @Test
    @DisplayName("若分词数大于最大分词数，返回最近的最大分词数大小的历史记录")
    void shouldOkWhenTokenCountOutOfLimit() {
        AippMemoryConfig config = new AippMemoryConfig();
        config.setWindowAlg("token_window");
        config.setSerializeAlg("full");
        config.setProperty(4);
        Memory memory = factory.create(config, genHistories(3));
        assertThat(memory).isInstanceOf(AippTokenWindowMemory.class);
        assertThat(memory.messages().size()).isEqualTo(4);
    }
}
