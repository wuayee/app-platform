/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;

/**
 * 知识库服务接口
 *
 * @author h00804153
 * @since 2024-04-23
 */
public interface KnowledgeService {
    /**
     * 获取知识库列表。
     *
     * @param cond 查询条件
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识库列表详细信息
     */
    PageResponse<KRepoDto> listKnowledgeRepo(KnowledgeQueryCondition cond, Integer pageNum, Integer pageSize);

    /**
     * 获取知识表列表。
     *
     * @param repoId 知识库 id
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识表列表详细信息
     */
    PageResponse<KTableDto> listKnowledgeTables(Long repoId, Integer pageNum, Integer pageSize);
}
