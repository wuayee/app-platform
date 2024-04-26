/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

/**
 * 表示携带媒体数据的 {@link MessageContent}。
 *
 * @author 易文渊
 * @since 2024-4-3
 */
public class MediaContent implements MessageContent {
    private final Media media;

    /**
     * 使用媒体url创建 {@link MediaContent} 的实例。
     *
     * @param url 表示媒体资源url的 {@link String}。
     */
    public MediaContent(String url) {
        this(url, null);
    }

    /**
     * 使用媒体base64编码数据和媒体类型创建 {@link MediaContent} 的实例。
     *
     * @param data 表示媒体数据base64编码的 {@link String}。
     * @param mimeType 描述消息内容类型，通用结构为 {@code type/subtype}的 {@link String}。
     */
    public MediaContent(String data, String mimeType) {
        this(new Media(data, mimeType));
    }

    /**
     * 使用{@link Media}创建 {@link MediaContent} 的实例。
     *
     * @param media 表示媒体数据的 {@link Media}。
     */
    public MediaContent(Media media) {
        this.media = media;
    }

    @Override
    public String data() {
        return this.media.getData();
    }

    public Media media() {
        return this.media;
    }
}