/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.embed;

import com.huawei.jade.fel.ModelOptions;

import lombok.Data;

/**
 * 表示嵌入模型参数的实体。
 *
 * @author 易文渊
 * @since 2024-04-24
 */
@Data
public class EmbedOptions implements ModelOptions {
    /**
     * 模型名。
     */
    private String model;

    /**
     * 模型接口秘钥。
     */
    private String apiKey;
}