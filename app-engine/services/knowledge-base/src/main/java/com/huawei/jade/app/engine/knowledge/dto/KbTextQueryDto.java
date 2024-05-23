/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * KbTextQueryDto 文本类型知识表查询参数
 *
 * @author YangPeng
 * @since 2024-05-23
 */
@Setter
@Getter
@Builder
public class KbTextQueryDto {
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
}
