/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.retrieve;

import com.huawei.fitframework.resource.web.Media;

import java.util.Map;
import java.util.Optional;

/**
 * 检索文档内容。
 *
 * @author 刘信宏
 * @since 2024-06-13
 */
public interface Document {
    /**
     * 获取文本内容。
     *
     * @return 返回拼接后文本内容的 {@link String}。
     */
    String text();

    /**
     * 获取元数据，如文档来源等。
     *
     * @return 返回媒体内容的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> meta();

    /**
     * 获取媒体内容。
     *
     * @return 表示媒体内容的 {@link Media}。
     */
    default Optional<Media> medias() {
        return Optional.empty();
    }
}
