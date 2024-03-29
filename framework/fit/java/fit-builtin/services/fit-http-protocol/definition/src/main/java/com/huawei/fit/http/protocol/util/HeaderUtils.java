/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.protocol.util;

import static com.huawei.fit.http.protocol.MessageHeaderNames.CONTENT_LENGTH;
import static com.huawei.fit.http.protocol.MessageHeaderNames.TRANSFER_ENCODING;
import static com.huawei.fit.http.protocol.MessageHeaderValues.CHUNKED;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.MessageHeaderValues;
import com.huawei.fit.http.protocol.MessageHeaders;

import java.util.Objects;

/**
 * 用于处理 Http 消息头的工具类。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-07
 */
public class HeaderUtils {
    /**
     * 根据指定的消息头，判断传输方式是否为 {@link MessageHeaderValues#CHUNKED}。
     *
     * @param headers 表示指定的消息头的 {@link MessageHeaders}。
     * @return 如果传输方式为 {@link MessageHeaderValues#CHUNKED}，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isChunked(MessageHeaders headers) {
        notNull(headers, "The headers cannot be null.");
        return headers.first(TRANSFER_ENCODING)
                .filter(transferEncoding -> Objects.equals(transferEncoding, CHUNKED))
                .isPresent();
    }

    /**
     * 根据指定的消息头，获取消息内容的长度。
     *
     * @param headers 表示指定的消息头的 {@link MessageHeaders}。
     * @return 表示消息内容长度的 {@code int}。
     */
    public static int contentLength(MessageHeaders headers) {
        notNull(headers, "The headers cannot be null.");
        return headers.first(CONTENT_LENGTH).map(Integer::parseInt).orElse(-1);
    }

    /**
     * 根据指定的消息头，获取消息内容的长度。
     *
     * @param headers 表示指定的消息头的 {@link MessageHeaders}。
     * @return 表示消息内容长度的 {@code long}。
     */
    public static long contentLengthLong(MessageHeaders headers) {
        notNull(headers, "The headers cannot be null.");
        return headers.first(CONTENT_LENGTH).map(Long::parseLong).orElse(-1L);
    }
}
