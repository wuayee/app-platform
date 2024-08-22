/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat.character;

import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fel.chat.MessageType;

import java.util.Collections;
import java.util.List;

/**
 * 表示人类消息的实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class HumanMessage extends AbstractChatMessage {
    private final List<Media> medias;

    /**
     * 通过文本信息来初始化 {@link HumanMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public HumanMessage(String text) {
        this(text, null);
    }

    /**
     * 通过文本和多媒体数据来初始化 {@link HumanMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     * @param medias 表示多媒体数据列表的 {@link List}{@code <}{@link Media}{@code >}。
     */
    public HumanMessage(String text, List<Media> medias) {
        super(text);
        this.medias = ObjectUtils.nullIf(medias, Collections.emptyList());
    }

    @Override
    public MessageType type() {
        return MessageType.HUMAN;
    }

    @Override
    public List<Media> medias() {
        return this.medias;
    }
}
