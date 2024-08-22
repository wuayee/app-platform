/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators;

import static modelengine.fel.engine.operators.patterns.SyncTipper.fewShot;
import static modelengine.fel.engine.operators.patterns.SyncTipper.history;
import static modelengine.fel.engine.operators.patterns.SyncTipper.question;
import static modelengine.fel.engine.operators.patterns.SyncTipper.value;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.utils.IdGenerator;

import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.flows.Conversation;
import modelengine.fel.engine.operators.patterns.SimplePattern;
import modelengine.fel.engine.operators.patterns.SyncTipper;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatMessages;
import modelengine.fel.chat.Prompt;
import modelengine.fel.core.examples.Example;
import modelengine.fel.core.examples.ExampleSelector;
import modelengine.fel.core.examples.support.DefaultExample;
import modelengine.fel.core.memory.CacheMemory;
import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.retriever.Retriever;
import modelengine.fel.core.template.MessageContent;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.util.AiFlowSession;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.stream.Collectors;

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
            .runnableParallel(SyncTipper.question(),
                SyncTipper.history("history"),
                SyncTipper.value("context", (arg -> MessageContent.from("context"))),
                SyncTipper.value("key", "val"))
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
    void shouldOkWhenAiFlowWithExampleSelector() {
        Example[] examples = {new DefaultExample("2+2", "4"), new DefaultExample("2+3", "5")};
        Conversation<String, Prompt> converse = AiFlows.<String>create()
                .runnableParallel(SyncTipper.question(),
                        SyncTipper.fewShot(ExampleSelector.builder()
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
    void shouldOkWhenAiFlowWithRetriever() {
        Memory memory = getMockMemory();
        Retriever<Prompt, MessageContent> retriever =
            input -> MessageContent.from("[context: " + input.text() + "]", new Media("image/png", "url"));
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, MessageContent> ragFlow = AiFlows.<Tip>create()
            .runnableParallel(SyncTipper.history(), SyncTipper.passThrough())
            .prompt(Prompts.human("enhance {{q1}} with {{history}}"))
            .retrieve(retriever)
            .close(r -> answer.append(r.text()));

        ChatMessages messages = new ChatMessages();
        AiFlows.<Tip>create()
            .runnableParallel(SyncTipper.value("context", ragFlow), SyncTipper.history("history"), SyncTipper.passThrough())
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
