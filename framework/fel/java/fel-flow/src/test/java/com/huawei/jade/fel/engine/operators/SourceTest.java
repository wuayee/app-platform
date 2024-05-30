/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.waterflow.domain.utils.SleepUtil;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.ConverseLatch;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.engine.operators.sources.Source;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Source} 的测试。
 *
 * @author 刘信宏
 * @since 2024-05-09
 */
public class SourceTest {
    @Test
    void shouldOkWhenFlowOfferSource() {
        AtomicInteger cnt = new AtomicInteger(0);
        StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{question}}"))
                .close(data -> cnt.incrementAndGet());

        Source<Tip> tipSource = new Source<>();
        ConverseLatch<Prompt> converseLatch = flow.converse()
                .doOnSuccess(data -> answer.append(data.text()))
                .offer(tipSource);
        tipSource.emit(Tip.from("question", "my question"));

        converseLatch.await();
        assertThat(answer.toString()).isEqualTo("my question");

        // 第二次会话
        StringBuilder answer2 = new StringBuilder();
        ConverseLatch<Prompt> converseLatch2 =
                flow.converse().doOnSuccess(data -> answer2.append(data.text())).offer(tipSource);
        tipSource.emit(Tip.from("question", "my question2"));

        converseLatch2.await();
        assertThat(answer2.toString()).isEqualTo("my question2");

        // 验证会话结束后注销热源的订阅
        SleepUtil.sleep(10);
        assertThat(cnt.get()).isEqualTo(2);
    }
}
