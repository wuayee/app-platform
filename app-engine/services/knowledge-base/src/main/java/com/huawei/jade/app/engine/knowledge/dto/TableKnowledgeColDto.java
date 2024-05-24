/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import com.huawei.jade.app.engine.knowledge.dto.enums.IndexType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 表格场景 知识表列信息
 *
 * @since 2024-05-22
 */
@Getter
@Setter
@NoArgsConstructor
public class TableKnowledgeColDto {
    /** 列名 */
    private String name;

    /** 数据类型 */
    private String dataType;

    /** 索引类型 */
    private IndexType indexType;

    /** 向量化服务Id */
    private Long embedServiceId;

    /** 描述 */
    private String desc;

    public TableKnowledgeColDto(String name) {
        this.name = name;
    }
}
