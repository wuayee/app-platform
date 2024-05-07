/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.common;

import java.util.List;

/**
 * 分词器接口。
 *
 * @since 2024-05-08
 */
public interface Tokenizer {
    /**
     * 分词器处理函数。
     *
     * @param text 表示传入数据的 {@link String}
     * @return 返回处理好的分词列表。
     */
    List<String> process(String text);
}
