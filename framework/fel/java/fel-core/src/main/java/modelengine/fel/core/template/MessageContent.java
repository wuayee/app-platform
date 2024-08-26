/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.template;

import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示消息内容的实体。
 *
 * @author 易文渊
 * @since 2024-06-17
 */
public final class MessageContent {
    private final String text;
    private final List<Media> medias;

    /**
     * 使用文本消息和媒体资源创建 {@link MessageContent} 的实例。
     *
     * @param text 表示文本消息的 {@link String}。
     * @param medias 表示媒体资源列表的 {@link List}{@code <}{@link Media}{@code >}。
     */
    public MessageContent(String text, List<Media> medias) {
        this.text = StringUtils.blankIf(text, StringUtils.EMPTY);
        this.medias = ObjectUtils.getIfNull(medias, Collections::emptyList);
    }

    /**
     * 使用媒体资源创建 {@link MessageContent} 的实例。
     *
     * @param medias 表示 {@code 0..n} 个媒体资源的 {@code Media[]}。
     * @return 表示创建成功的 {@link MessageContent}。
     */
    public static MessageContent from(Media... medias) {
        return new MessageContent(null, Arrays.asList(medias));
    }

    /**
     * 使用文本消息和媒体资源创建 {@link MessageContent} 的实例。
     *
     * @param text 表示文本消息的 {@link String}。
     * @param medias 表示 {@code 0..n} 个媒体资源的 {@code Media[]}。
     * @return 表示创建成功的 {@link MessageContent}。
     */
    public static MessageContent from(String text, Media... medias) {
        return new MessageContent(text, Arrays.asList(medias));
    }

    /**
     * 获取文本内容。
     *
     * @return 返回拼接后文本内容的 {@link String}。
     */
    public String text() {
        return this.text;
    }

    /**
     * 获取媒体内容。
     *
     * @return 返回媒体内容的 {@link List}{@code <}{@link Media}{@code >}。
     */
    public List<Media> medias() {
        return this.medias;
    }
}