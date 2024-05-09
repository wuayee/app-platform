/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.content.Content;
import com.huawei.jade.fel.chat.content.MediaContent;
import com.huawei.jade.fel.chat.content.MessageContent;
import com.huawei.jade.fel.chat.content.TextContent;
import com.huawei.jade.fel.core.template.MessageTemplate;
import com.huawei.jade.fel.core.template.StringTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link MessageTemplate} 的抽象实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public abstract class AbstractMessageTemplate implements MessageTemplate {
    private final StringTemplate template;

    /**
     * 使用字符串模板创建 {@link AbstractMessageTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     */
    protected AbstractMessageTemplate(StringTemplate template) {
        this.template = template;
    }

    @Override
    public ChatMessage render(Map<String, MessageContent> values) {
        Map<String, MessageContent> args = ObjectUtils.getIfNull(values, Collections::emptyMap);
        Stream<Content> textStream = Stream.of(args)
                .map(v -> v.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().text())))
                .map(this.template::render)
                .map(TextContent::new);
        Stream<Content> mediaStream = this.template.placeholder()
                .stream()
                .map(args::get)
                .filter(Objects::nonNull)
                .flatMap(cs -> cs.medias().stream().map(MediaContent::new));
        return this.collect(Stream.concat(textStream, mediaStream));
    }

    @Override
    public Set<String> placeholder() {
        return this.template.placeholder();
    }

    /**
     * 收集消息内容流生成 {@link ChatMessage}。
     *
     * @param contentStream 表示消息内容流的 {@link Stream}{@code <}{@link Content}{@code >}。
     * @return 返回表示聊天消息的 {@link ChatMessage}。
     */
    protected abstract ChatMessage collect(Stream<Content> contentStream);
}