/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.embed;

import lombok.Data;

import java.util.List;

/**
 * 表示生成嵌入请求的实体。
 *
 * @author 易文渊
 * @since 2024-04-13
 */
@Data
public class EmbedRequest {
    /**
     * 输入字符串列表。
     */
    private List<String> inputs;

    /**
     * 嵌入模型参数。
     */
    private EmbedOptions options;
}