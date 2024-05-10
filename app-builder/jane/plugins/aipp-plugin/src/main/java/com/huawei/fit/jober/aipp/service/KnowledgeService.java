/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.KnowledgeDetailDto;

import java.io.IOException;

/**
 * 知识库服务接口
 *
 * @author h00804153
 * @since 2024-04-23
 */
public interface KnowledgeService {
    /**
     * 查询知识库列表
     *
     * @param cond 过滤条件
     * @param page 分页
     * @return 知识库概况
     */
    PageResponse<KnowledgeDetailDto> listKnowledge(KnowledgeQueryCondition cond, PaginationCondition page)
            throws IOException;
}
