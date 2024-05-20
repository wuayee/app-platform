/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * KGenerateConfigDto 导入文件的配置信息参数
 *
 * @author YangPeng
 * @since 2024-05-17 16:16
 */
@Getter
@Setter
public class KGenerateConfigDto {
    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 知识表id
     */
    private Long tableId;

    /**
     * 导入文件名列表
     */
    private List<String> fileNames;

    /**
     * 切片模式
     */
    private String sliceMode;

    /**
     * 切片大小
     */
    private Integer chunkSize;

    /**
     * 切片重叠长度
     */
    private Integer chunkOverlap;

    /**
     * 文本清洗算子列表
     */
    private List<String> operatorIds;

    /**
     * embedding服务
     */
    private String embeddingUrl;

    /**
     * 向量数据库服务
     */
    private String vectorUrl;
}
