/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.service.KnowledgeService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;

/**
 * 知识库服务相关接口。
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/knowledge", group = "知识库相关操作")
public class KnowledgeController extends AbstractController {
    private final KnowledgeService knowledgeService;

    /**
     * 构造函数，初始化认证器和知识库服务。
     *
     * @param authenticator 认证器
     * @param knowledgeService 知识库服务
     */
    public KnowledgeController(Authenticator authenticator, KnowledgeService knowledgeService) {
        super(authenticator);
        this.knowledgeService = knowledgeService;
    }

    /**
     * 获取知识库列表。
     *
     * @param httpRequest Http 请求
     * @param cond 检索条件
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识库列表详细信息
     */
    @GetMapping(path = "/repos", description = "获取知识库列表")
    public Rsp<PageResponse<KRepoDto>> listKnowledgeRepo(HttpClassicServerRequest httpRequest,
            @RequestBean KnowledgeQueryCondition cond,
            @RequestQuery(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Rsp.ok(this.knowledgeService.listKnowledgeRepo(cond, pageNum, pageSize));
    }

    /**
     * 根据知识库 id 获取知识表列表。
     *
     * @param httpRequest Http 请求
     * @param repoId 知识库 id
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识表列表详细信息
     */
    @GetMapping(path = "/repos/{repo_id}/tables", description = "根据知识库 id 获取知识表列表")
    public Rsp<PageResponse<KTableDto>> listKnowledgeTables(HttpClassicServerRequest httpRequest,
            @PathVariable("repo_id") Long repoId,
            @RequestQuery(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Rsp.ok(this.knowledgeService.listKnowledgeTables(repoId, pageNum, pageSize));
    }
}
