/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.http;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.serialization.TagLengthValues;

/**
 * 表示 Websocket 通信过程中通过 TLV 来传输流式调用信息的工具类。
 *
 * @author 何天放 h00679269
 * @since 2024-04-15
 */
public class WebsocketUtils {
    /** 流式调用过程中通过 websocket 通道传递的消息中各字段的标识，与 http 调用中请求头中的消息位于不同的通道，与其不会产生标识的冲突 */
    private static final int STREAM_MESSAGE_INDEX_TAG = 0x80;
    private static final int STREAM_MESSAGE_TYPE_TAG = 0x81;
    private static final int STREAM_MESSAGE_CONTENT_TAG = 0x82;

    /**
     * 从 TLV 中获取索引。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示索引的 {@code int}。
     */
    public static int getIndex(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return Integer.parseInt(new String(tagValues.getValue(STREAM_MESSAGE_INDEX_TAG), UTF_8));
    }

    /**
     * 向 TLV 中设置索引。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param index 表示索引的 {@code int}。
     */
    public static void setIndex(TagLengthValues tagValues, int index) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(index, "The index cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_INDEX_TAG, Integer.toString(index).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取类型信息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示类型信息的 {@code int}。
     */
    public static int getType(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        try {
            return Integer.parseInt(new String(tagValues.getValue(STREAM_MESSAGE_TYPE_TAG), UTF_8));
        } catch (NumberFormatException ignored) {
            return StreamMessageType.UNKNOWN.code();
        }
    }

    /**
     * 向 TLV 中设置类型信息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param type 表示类型信息的 {@code int}。
     */
    public static void setType(TagLengthValues tagValues, int type) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(type, "The type cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_TYPE_TAG, Integer.toString(type).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取内容。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示内容的 {@code byte[]}。
     */
    public static byte[] getContent(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return tagValues.getValue(STREAM_MESSAGE_CONTENT_TAG);
    }

    /**
     * 向 TLV 中设置内容。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param type 表示内容的 {@code byte[]}。
     */
    public static void setContent(TagLengthValues tagValues, byte[] type) {
        notNull(tagValues, "The TLV cannot be null.");
        notNull(type, "The content cannot be null.");
        tagValues.putTag(STREAM_MESSAGE_CONTENT_TAG, type);
    }
}
