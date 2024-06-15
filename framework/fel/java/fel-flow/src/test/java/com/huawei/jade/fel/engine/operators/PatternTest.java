/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.history;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.passThrough;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.question;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.value;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.MediaContent;
import com.huawei.jade.fel.chat.content.MessageContent;
import com.huawei.jade.fel.chat.content.TextContent;
import com.huawei.jade.fel.core.memory.CacheMemory;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.Conversation;
import com.huawei.jade.fel.engine.operators.patterns.SimplePattern;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.engine.util.StateKey;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

/**
 * 委托单元测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class PatternTest {
    @Test
    void shouldOkWhenAiFlowWithNormalRunnableParallel() {
        Memory memory = getMockMemory();
        final StringBuilder answer = new StringBuilder();
        Conversation<String, Prompt> converse = AiFlows.<String>create()
                .runnableParallel(question(),
                        history("history"),
                        value("context", (arg -> Contents.from("context"))),
                        value("key", "val"))
                .prompt(Prompts.human("answer {{question}} from {{context}} with {{history}}"))
                .close()
                .converse()
                .bind(memory);

        converse.doOnSuccess(r -> answer.append(r.text())).offer("question").await();
        assertThat(answer.toString()).isEqualTo("answer question from context with my history");

        // 验证 runnableParallel 中 join 初始值重新获取，不影响后续的请求。
        StringBuilder answer1 = new StringBuilder();
        converse.doOnSuccess(r -> answer1.append(r.text())).offer("question1").await();
        assertThat(answer1.toString()).isEqualTo("answer question1 from context with my history");
    }

    @Test
    void shouldOkWhenAiFlowWithRetriever() {
        Memory memory = getMockMemory();
        Retriever<Prompt, MessageContent> retriever =
                input -> Contents.from(new TextContent("[context: " + input.text() + "]"), new MediaContent("url"));
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, MessageContent> ragFlow = AiFlows.<Tip>create()
                .runnableParallel(history(), passThrough())
                .prompt(Prompts.human("enhance {{q1}} with {{history}}"))
                .retrieve(retriever)
                .close(r -> answer.append(r.text()));

        ChatMessages messages = new ChatMessages();
        AiFlows.<Tip>create()
                .runnableParallel(value("context", ragFlow), history("history"), passThrough())
                .prompt(Prompts.human("answer {{q1}} and {{q2}} from {{context}} with {{history}}"))
                .close(r -> messages.addAll(r.messages()))
                .converse()
                .bind(memory)
                .offer(Tip.from("q1", "my question1").add("q2", "my question2"))
                .await();

        assertThat(answer.toString()).isEqualTo("[context: enhance my question1 with my history]");
        assertThat(messages.text()).isEqualTo(String.format(
                "answer my question1 and my question2 from %s with my history",
                answer));
        assertThat(messages.medias()).hasSize(1);
    }

    @Test
    void shouldOkWhenDelegatePatternWithBindingArgs() {
        FlowSession session = new FlowSession();
        Map<String, Object> argsInit = MapBuilder.<String, Object>get().put("key", "value").build();
        SimplePattern<Prompt, String> pattern =
            new SimplePattern<Prompt, String>(
                (prompt, args) -> {
                    String value = Optional.ofNullable(ObjectUtils.<String>cast(args.get("key")))
                            .orElse("empty");
                    String sessionId = ObjectUtils.<FlowSession>cast(args.get(StateKey.FLOW_SESSION)).getId();
                    return prompt.text() + value + sessionId;
                }).bind(argsInit);
        String result = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{0}}"))
                .delegate(pattern)
                .close()
                .converse(session)
                .offer(Tip.fromArray("human msg."))
                .await();

        // pattern 已绑定的参数被后续的bind操作清空
        assertThat(result).isEqualTo("human msg.empty" + session.getId());
    }

    private static Memory getMockMemory() {
        return new CacheMemory() {
            @Override
            public String text() {
                return "my history";
            }
        };
    }
}
