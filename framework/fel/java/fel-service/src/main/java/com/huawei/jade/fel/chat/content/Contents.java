/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示消息内容集合的实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class Contents {
    private final List<MessageContent> payload;

    private Contents(List<MessageContent> payload) {
        this.payload = payload;
    }

    /**
     * 使用文本消息创建 {@link Contents} 的实例。
     *
     * @param text 表示文本消息的 {@link String}。
     * @return 表示创建成功的 {@link Contents}。
     */
    public static Contents from(String text) {
        return new Contents(Collections.singletonList(new TextContent(text)));
    }

    /**
     * 使用 {@link MediaContent}{@code []} 创建 {@link Contents} 的实例。
     *
     * @param content 表示消息内容列表的 {@link MediaContent}{@code []}。
     * @return 表示创建成功的 {@link Contents}。
     */
    public static Contents from(MessageContent... content) {
        return Contents.from(Arrays.asList(content));
    }

    /**
     * 使用 {@link List}{@code <}{@link MediaContent}{@code >} 创建 {@link Contents} 的实例。
     *
     * @param contentList 表示消息内容列表的 {@link MediaContent}{@code []}。
     * @return 表示创建成功的 {@link Contents}。
     */
    public static Contents from(List<MessageContent> contentList) {
        return new Contents(contentList);
    }

    /**
     * 获取文本内容，如果存在多条内容，则使用{@code \n}进行拼接。
     *
     * @return 返回拼接后文本内容的 {@link String}。
     */
    public String text() {
        return this.payload.stream()
                .filter(c -> c instanceof TextContent)
                .map(MessageContent::data)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 获取媒体内容。
     *
     * @return 返回媒体内容的 {@link List}{@code <}{@link Media}{@code >}。
     */
    public List<Media> medias() {
        return this.payload.stream()
                .filter(c -> c instanceof MediaContent)
                .map(c -> ((MediaContent) c).media())
                .collect(Collectors.toList());
    }
}
