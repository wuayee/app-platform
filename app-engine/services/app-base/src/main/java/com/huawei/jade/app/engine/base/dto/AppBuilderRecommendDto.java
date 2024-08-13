/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.base.dto;

import lombok.Data;

/**
 * 获取猜你想问数据body
 *
 * @author 杨海波
 * @since 2024-05-25
 */
@Data
public class AppBuilderRecommendDto {
    // 上一条问答对
    private String question;
    private String answer;
    private String model;
}
