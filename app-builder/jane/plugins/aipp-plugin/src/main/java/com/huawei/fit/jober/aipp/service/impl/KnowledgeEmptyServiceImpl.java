/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.KnowledgeDetailDto;
import com.huawei.fit.jober.aipp.service.KnowledgeService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * 临时屏蔽知识库服务
 *
 * @author 黄夏露 h00804153
 * @since 2024-05-10
 */
@Component
public class KnowledgeEmptyServiceImpl implements KnowledgeService {
    @Override
    public PageResponse<KnowledgeDetailDto> listKnowledge(KnowledgeQueryCondition cond, PaginationCondition page)
            throws IOException {
        return new PageResponse<>(0L, null, Collections.emptyList());
    }
}
