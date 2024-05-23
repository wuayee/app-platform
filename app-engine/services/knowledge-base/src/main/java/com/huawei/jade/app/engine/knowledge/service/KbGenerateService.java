/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.KbChunkQueryDto;
import com.huawei.jade.app.engine.knowledge.dto.KbGenerateConfigDto;
import com.huawei.jade.app.engine.knowledge.dto.TableKnowledgeColDto;
import com.huawei.jade.app.engine.knowledge.params.TableKnowledgeParam;
import com.huawei.jade.app.engine.knowledge.vo.PageResultVo;

import java.util.List;

/**
 * KbGenerateService 知识生成
 *
 * @author YangPeng
 * @since 2024-05-20 11:10
 */
public interface KbGenerateService {
    /**
     * 导入文本类型知识
     *
     * @param configDto 配置信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KbGenerateService.importKnowledge")
    void importKnowledge(KbGenerateConfigDto configDto);

    /**
     * 获取向量知识信息
     *
     * @param chunkQueryDto 查询参数
     * @return 查询结果
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KbGenerateService.getChunks")
    PageResultVo<String> getChunks(KbChunkQueryDto chunkQueryDto);

    /**
     * 导入表格类型知识
     *
     * @param param 配置信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KbGenerateService.createTableKnowledge")
    void createTableKnowledge(TableKnowledgeParam param);

    /**
     * 查询列信息
     *
     * @param param 配置信息
     * @return 列信息
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.KbGenerateService.getTableKnowledgeColumns")
    List<TableKnowledgeColDto> getTableKnowledgeColumns(TableKnowledgeParam param);
}
