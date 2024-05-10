/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.KnowledgeDetailDto;
import com.huawei.fit.jober.aipp.service.KnowledgeService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;

import java.io.IOException;

/**
 * 知识库服务相关接口
 *
 * @author h00804153
 * @since 2024-04-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/knowledge", group = "知识库相关操作")
public class KnowledgeController extends AbstractController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(Authenticator authenticator, KnowledgeService knowledgeService) {
        super(authenticator);
        this.knowledgeService = knowledgeService;
    }

    @GetMapping(description = "查询知识库列表")
    public Rsp<PageResponse<KnowledgeDetailDto>> listKnowledge(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBean KnowledgeQueryCondition cond,
            @RequestBean @Validated PaginationCondition page) throws IOException {
        return Rsp.ok(this.knowledgeService.listKnowledge(cond, page));
    }
}
