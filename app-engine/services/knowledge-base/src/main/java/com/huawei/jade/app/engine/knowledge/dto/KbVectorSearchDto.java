/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * KbVectorSearchDto 文本型知识表多源检索参数
 *
 * @author WangZifan
 * @since 2024-05-29
 */
@Getter
@Setter
public class KbVectorSearchDto {
    /**
     * 知识表id
     */
    private List<Long> tableId;

    /**
     * 查询关键字
     */
    private String content;

    /**
     * topK
     */
    private Integer topK;

    /**
     * 阈值
     */
    private Float threshold;
}
