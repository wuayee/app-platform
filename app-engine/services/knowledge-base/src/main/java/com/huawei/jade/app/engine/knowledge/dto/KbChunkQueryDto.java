/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 向量化知识内容检索参数类
 *
 * @since 2024-05-21
 */
@Getter
@Setter
public class KbChunkQueryDto {
    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 知识表id
     */
    private Long tableId;

    /**
     * 页数
     */
    private Integer pageNo;

    /**
     * 页面大小
     */
    private Integer pageSize;

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
    private Integer threshold;

    /**
     * 列
     */
    private Integer columnId;

    /**
     * embedding服务
     */
    private String embeddingUrl;

    /**
     * 向量数据库服务
     */
    private String vectorUrl;
}
