/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static com.huawei.jade.fel.utils.FlowsTestUtils.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.engine.operators.sources.Source;

import org.junit.jupiter.api.Test;

/**
 * {@link Source} 的测试。
 *
 * @author 刘信宏
 * @since 2024-05-09
 */
public class SourceTest {
    @Test
    void shouldOkWhenFlowOfferSource() {
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.sys("{{someone}}"), Prompts.human("{{question}}"))
                .close(r -> answer.append(r.get().getData().text()));

        Source<Tip> tipSource = new Source<>();
        flow.offer(tipSource);
        tipSource.emit(new Tip().add("someone", "will").add("question", "my question"));

        waitUntil(() -> answer.length() != 0, 1000);
        assertThat(answer.toString()).isEqualTo("will\nmy question");
    }
}
