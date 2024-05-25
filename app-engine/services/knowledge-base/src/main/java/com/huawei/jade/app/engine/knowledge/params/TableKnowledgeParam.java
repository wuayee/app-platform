/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.params;

import com.huawei.jade.app.engine.knowledge.dto.TableKnowledgeColDto;
import com.huawei.jade.app.engine.knowledge.service.param.PageQueryParam;
import com.huawei.jade.app.engine.knowledge.utils.DecodeUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 表格场景 知识表真实数据表格创建参数
 *
 * @since 2024-05-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableKnowledgeParam extends PageQueryParam {
    /** 知识库Id */
    private Long repositoryId;

    /** 知识表Id */
    private Long knowledgeTableId;

    /** 表头行 */
    private Integer headerLine = 0;

    /** 数据起始行 */
    private Integer startRow = 1;

    /** 工作表ID */
    private Integer sheetId = 0;

    /** 列 */
    private List<TableKnowledgeColDto> columns;

    /** 文件名 */
    private String fileName;

    /**
     * 获取文件名称
     *
     * @return 解码后的文件名称
     */
    public String getFileName() {
        return DecodeUtil.decodeStr(fileName);
    }
}
