/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

/**
 * 表示消息内容。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public interface Content {
    /**
     * 获取消息内容中的数据。
     *
     * @return 表示消息数据的 {@link String}。
     */
    String data();
}
