/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.http;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.StringUtils;

/**
 * 表示 Websocket 通信过程中构造响应式调用异常终结信号内容的工具类。
 * <p>异常终结消息中各字段的标识与 http 调用中请求头以及流式调用消息位于不同通道，与前两者不会产生标识的冲突。</p>
 *
 * @author 何天放 h00679269
 * @since 2024-04-17
 */
public class FailMessageContentUtils {
    private static final int CODE_TAG = 0x00;
    private static final int MESSAGE_TAG = 0x01;

    /**
     * 从 TLV 中获取错误码。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示错误码的 {@code int}。
     */
    public static int getCode(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return Integer.parseInt(new String(tagValues.getValue(CODE_TAG), UTF_8));
    }

    /**
     * 向 TLV 中设置错误码。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param code 表示错误码的 {@code int}。
     */
    public static void setCode(TagLengthValues tagValues, int code) {
        notNull(tagValues, "The TLV cannot be null.");
        tagValues.putTag(CODE_TAG, Integer.toString(code).getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取异常消息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异常消息的 {@link String}。
     */
    public static String getMessage(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(MESSAGE_TAG), UTF_8);
    }

    /**
     * 向 TLV 中设置异常消息。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param message 表示错误码的 {@link String}。
     */
    public static void setMessage(TagLengthValues tagValues, String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        tagValues.putTag(MESSAGE_TAG, message.getBytes(UTF_8));
    }
}
