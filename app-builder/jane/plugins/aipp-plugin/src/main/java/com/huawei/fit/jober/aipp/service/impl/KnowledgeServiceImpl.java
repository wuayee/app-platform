/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.service.KnowledgeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;
import com.huawei.jade.app.engine.knowledge.params.RepoQueryParam;
import com.huawei.jade.app.engine.knowledge.service.KRepoService;
import com.huawei.jade.app.engine.knowledge.service.KTableService;
import com.huawei.jade.app.engine.knowledge.service.param.PageQueryParam;

/**
 * 知识库服务接口实现
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Component
public class KnowledgeServiceImpl implements KnowledgeService {
    private final KRepoService kRepoService;
    private final KTableService kTableService;

    public KnowledgeServiceImpl(KRepoService kRepoService, KTableService kTableService) {
        // 独立环境的 eDataMate.query_list_url 暂时由外部环境注入，之后统一整改到配置文件中
        this.kRepoService = kRepoService;
        this.kTableService = kTableService;
    }

    @Override
    public PageResponse<KRepoDto> listKnowledgeRepo(KnowledgeQueryCondition cond, Integer pageNum, Integer pageSize) {
        RepoQueryParam param = this.buildRepoQueryParam(pageNum, pageSize, cond);
        return new PageResponse<>(this.safelyConvertToLongType(this.kRepoService.queryReposCount(param)),
                null,
                this.kRepoService.queryReposByName(param));
    }

    private RepoQueryParam buildRepoQueryParam(Integer pageNum, Integer pageSize, KnowledgeQueryCondition cond) {
        RepoQueryParam param = new RepoQueryParam();
        param.setOffset(pageNum);
        param.setSize(pageSize);
        if (StringUtils.isNotEmpty(cond.getName())) {
            param.setName(cond.getName());
        }
        return param;
    }

    @Override
    public PageResponse<KTableDto> listKnowledgeTables(Long repoId, Integer pageNum, Integer pageSize) {
        PageQueryParam param = new PageQueryParam();
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        return new PageResponse<>(this.safelyConvertToLongType(this.kTableService.getTableCountByRepoId(repoId)),
                null,
                this.kTableService.getByRepoId(repoId, param));
    }

    private Long safelyConvertToLongType(Number value) {
        return value.longValue();
    }
}
