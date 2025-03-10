/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import static modelengine.fit.jade.aipp.memory.AippMemorySerializeAlg.FULL;
import static modelengine.fit.jade.aipp.memory.AippMemorySerializeAlg.QUESTION_ONLY;
import static modelengine.fit.jade.aipp.memory.AippMemoryWindowAlg.BUFFER_WINDOW;
import static modelengine.fit.jade.aipp.memory.AippMemoryWindowAlg.TOKEN_WINDOW;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.template.BulkStringTemplate;
import modelengine.fel.core.template.support.DefaultBulkStringTemplate;
import modelengine.fel.core.tokenizer.Tokenizer;
import modelengine.fit.jade.aipp.memory.support.AippBufferWindowMemory;
import modelengine.fit.jade.aipp.memory.support.AippTokenWindowMemory;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.MapBuilder;

import java.util.List;
import java.util.Map;

/**
 * 表示历史消息工厂的默认实现。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
@Component
public class DefaultAippMemoryFactory implements AippMemoryFactory {
    private final Map<AippMemorySerializeAlg, BulkStringTemplate> templateMap;
    private final Map<AippMemoryWindowAlg, AippMemoryInitializer> initializerMap;

    /**
     * 创建 {@link DefaultAippMemoryFactory} 的实例。
     *
     * @param tokenizer 表示分词器的 {@link Tokenizer}。
     */
    public DefaultAippMemoryFactory(Tokenizer tokenizer) {
        this.templateMap = MapBuilder.<AippMemorySerializeAlg, BulkStringTemplate>get()
                .put(FULL, new DefaultBulkStringTemplate("Q: {{question}}\nA: {{answer}}", "\n"))
                .put(QUESTION_ONLY, new DefaultBulkStringTemplate("Q: {{question}}", "\n"))
                .build();
        AippMemoryInitializer bufferWindowInitializer =
                (rounds, property, template) -> new AippBufferWindowMemory(rounds, cast(property), template);
        AippMemoryInitializer tokenWindowInitializer =
                (rounds, property, template) -> new AippTokenWindowMemory(rounds, cast(property), template, tokenizer);
        this.initializerMap = MapBuilder.<AippMemoryWindowAlg, AippMemoryInitializer>get()
                .put(BUFFER_WINDOW, bufferWindowInitializer)
                .put(TOKEN_WINDOW, tokenWindowInitializer)
                .build();
    }

    @Override
    public Memory create(AippMemoryConfig config, List<AippChatRound> histories) {
        notNull(config, "The config cannot be null.");
        AippMemorySerializeAlg serializeAlg = AippMemorySerializeAlg.from(config.getSerializeAlg());
        BulkStringTemplate template = this.templateMap.get(serializeAlg);
        AippMemoryWindowAlg windowAlg = AippMemoryWindowAlg.from(config.getWindowAlg());
        AippMemoryInitializer initializer = this.initializerMap.get(windowAlg);
        return initializer.create(histories, config.getProperty(), template);
    }
}