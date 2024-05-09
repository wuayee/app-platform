/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

import java.util.List;

/**
 * 消息内容集合的接口。
 *
 * @author 刘信宏
 * @since 2024-05-06
 */
public interface MessageContent {
    /**
     * 获取文本内容。
     *
     * @return 返回拼接后文本内容的 {@link String}。
     */
    String text();

    /**
     * 获取媒体内容。
     *
     * @return 返回媒体内容的 {@link List}{@code <}{@link Media}{@code >}。
     */
    List<Media> medias();
}
