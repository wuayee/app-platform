/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.vo;

import com.huawei.jade.app.engine.knowledge.dto.TableKnowledgeColDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 表格型知识检索结果
 *
 * @since 2024/5/24
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableKnowledgeResult {
    /** 总数 */
    long count;

    /** 列属性 */
    private List<TableKnowledgeColDto> columns;

    /** 知识内容 */
    private List<List<Object>> list;
}
