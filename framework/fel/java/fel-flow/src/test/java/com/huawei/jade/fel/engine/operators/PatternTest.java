/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.fewShot;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.history;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.passThrough;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.question;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.value;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.utils.IdGenerator;
import com.huawei.fitframework.resource.web.Media;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.core.chat.ChatMessage;
import com.huawei.jade.fel.core.chat.Prompt;
import com.huawei.jade.fel.core.chat.support.ChatMessages;
import com.huawei.jade.fel.core.document.Content;
import com.huawei.jade.fel.core.document.Document;
import com.huawei.jade.fel.core.document.MeasurableDocument;
import com.huawei.jade.fel.core.fewshot.Example;
import com.huawei.jade.fel.core.fewshot.ExampleSelector;
import com.huawei.jade.fel.core.fewshot.support.DefaultExample;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.memory.support.CacheMemory;
import com.huawei.jade.fel.core.pattern.Retriever;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.Conversation;
import com.huawei.jade.fel.engine.operators.patterns.SimplePattern;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.engine.util.AiFlowSession;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 委托单元测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
@DisplayName("测试委托单元")
public class PatternTest {
    @Test
    @DisplayName("测试 RunnableParallel")
    void shouldOkWhenAiFlowWithNormalRunnableParallel() {
        Memory memory = getMockMemory();
        final StringBuilder answer = new StringBuilder();
        Conversation<String, Prompt> converse = AiFlows.<String>create()
                .runnableParallel(question(),
                        history("history"),
                        value("context", (arg -> Content.from("context"))),
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
    @DisplayName("测试 ExampleSelector")
    void shouldOkWhenAiFlowWithExampleSelector() {
        Example[] examples = {new DefaultExample("2+2", "4"), new DefaultExample("2+3", "5")};
        Conversation<String, Prompt> converse = AiFlows.<String>create()
                .runnableParallel(question(),
                        fewShot(ExampleSelector.builder()
                                .template("{{q}}={{a}}", "q", "a")
                                .delimiter("\n")
                                .example(examples)
                                .build()))
                .prompt(Prompts.human("{{examples}}\n{{question}}="))
                .close()
                .converse();
        assertThat(converse.offer("1+2").await().text()).isEqualTo("2+2=4\n2+3=5\n1+2=");
    }

    @Test
    @DisplayName("测试 Retriever")
    void shouldOkWhenAiFlowWithRetriever() {
        Memory memory = getMockMemory();
        Retriever<Prompt, MeasurableDocument> retriever =
                input -> Collections.singletonList(new MeasurableDocument(Document.custom()
                        .text("[context: " + input.text() + "]")
                        .medias(Collections.singletonList(new Media("image/png", "url")))
                        .metadata(Collections.emptyMap())
                        .build(), 0.6f));
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, Content> ragFlow = AiFlows.<Tip>create()
                .runnableParallel(history(), passThrough())
                .prompt(Prompts.human("enhance {{q1}} with {{history}}"))
                .retrieve(retriever)
                .enhance(d -> d)
                .synthesize(d -> d.get(0))
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
        assertThat(messages.messages()
                .stream()
                .map(ChatMessage::medias)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())).hasSize(1);
    }

    @Test
    @DisplayName("测试 SimplePattern")
    void shouldOkWhenDelegateSimplePattern() {
        FlowSession session = new FlowSession();
        SimplePattern<Prompt, String> pattern = new SimplePattern<>(prompt -> {
            String sessionId = AiFlowSession.get().map(IdGenerator::getId).orElse(StringUtils.EMPTY);
            return prompt.text() + sessionId;
        });
        String result = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{0}}"))
                .delegate(pattern)
                .close()
                .converse(session)
                .offer(Tip.fromArray("human msg."))
                .await();

        assertThat(result).isEqualTo("human msg." + session.getId());
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
