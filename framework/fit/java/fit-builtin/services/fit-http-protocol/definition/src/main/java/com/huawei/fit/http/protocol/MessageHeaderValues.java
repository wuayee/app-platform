/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.protocol;

/**
 * Http 常用消息头的值。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-04
 */
public class MessageHeaderValues {
    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-3.6.1">RFC 2616</a> */
    public static final String CHUNKED = "chunked";

    /** 用于 {@link MessageHeaderNames#CONTENT_DISPOSITION}，表示附件需要下载。 */
    public static final String ATTACHMENT = "attachment";

    /** 用于 {@link MessageHeaderNames#CONTENT_DISPOSITION}，表示附件需要预览。 */
    public static final String INLINE = "inline";
}
