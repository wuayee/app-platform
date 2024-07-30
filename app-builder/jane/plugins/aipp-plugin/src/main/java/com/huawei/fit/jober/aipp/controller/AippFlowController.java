/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.PutMapping;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.AippQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDetailDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AippOverviewRspDto;
import com.huawei.fit.jober.aipp.dto.AippVersionDto;
import com.huawei.fit.jober.aipp.service.AippFlowService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.validation.Validated;

import java.util.List;

/**
 * Aipp编排管理接口
 *
 * @author l00611472
 * @since 2023-12-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/aipp-info", group = "aipp编排管理接口")
public class AippFlowController extends AbstractController {
    private final AippFlowService aippFlowService;

    /**
     * AippFlowController
     *
     * @param authenticator authenticator
     * @param aippFlowService aippFlowService
     */
    public AippFlowController(Authenticator authenticator, @Fit AippFlowService aippFlowService) {
        super(authenticator);
        this.aippFlowService = aippFlowService;
    }

    /**
     * 查询aipp列表
     *
     * @param tenantId 租户id
     * @param cond 过滤条件
     * @param page 分页
     * @param httpRequest 操作上下文
     * @return aipp 概况
     */
    @GetMapping(description = "批量查询aipp")
    public Rsp<PageResponse<AippOverviewRspDto>> listAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBean AippQueryCondition cond,
            @RequestBean @Validated PaginationCondition page) {
        return Rsp.ok(aippFlowService.listAipp(cond, page, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param tenantId tenantId
     * @param version 版本
     * @param httpRequest http操作上下文
     * @return aipp 详情
     */
    @GetMapping(value = "/{aipp_id}", description = "查询aipp详情")
    public Rsp<AippDetailDto> queryAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam("version") String version) {
        return this.aippFlowService.queryAippDetail(aippId, version, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 创建aipp
     *
     * @param tenantId 租户id
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @PostMapping(description = "创建aipp")
    public Rsp<AippCreateDto> createAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AippDto aippDto) {
        return Rsp.ok(aippFlowService.create(aippDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param tenantId tenantId
     * @param baselineVersion 基线版本
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @DeleteMapping(path = "/{aipp_id}", description = "删除aipp")
    public Rsp<Integer> deleteAipp(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") String aippId, @RequestParam("version") String baselineVersion) {
        aippFlowService.deleteAipp(aippId, baselineVersion, this.contextOf(httpRequest, tenantId));
        return Rsp.ok(1);
    }

    /**
     * 更新aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @PutMapping(path = "/{aipp_id}", description = "更新aipp")
    public Rsp<AippCreateDto> updateAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestBody AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.aippFlowService.update(aippDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 发布aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param aippDto aipp定义
     * @param version aipp版本, 可选
     * @param httpRequest 操作上下文
     * @return 发布aipp概况
     */
    @PostMapping(path = "/{aipp_id}", description = "发布aipp")
    public Rsp<AippCreateDto> publishAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam(value = "version") String version, @RequestBody @Validated AippDto aippDto) {
        aippDto.setId(aippId);
        aippDto.setVersion(version);
        return this.aippFlowService.publish(aippDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 预览aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param baselineVersion aipp 的基线版本
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return 创建预览aipp的id和version
     */
    @PostMapping(path = "/{aipp_id}/preview", description = "预览aipp")
    public Rsp<AippCreateDto> previewAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam("version") String baselineVersion, @RequestBody AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.aippFlowService.previewAipp(baselineVersion,
                aippDto,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 退出预览
     *
     * @param tenantId 租户id
     * @param previewAippId aippId
     * @param previewVersion aipp preview version
     * @param httpRequest 操作上下文
     * @return Void
     */
    @DeleteMapping(path = "/{aipp_id}/preview", description = "退出预览")
    public Rsp<Void> cleanPreviewAipp(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") String previewAippId, @RequestParam("preview_version") String previewVersion) {
        this.aippFlowService.cleanPreviewAipp(previewAippId, previewVersion, contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 升级 aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param baselineVersion aipp baseline version
     * @param aippDto 升级aipp定义
     * @param httpRequest 操作上下文
     * @return Void
     */
    @PostMapping(path = "/{aipp_id}/upgrade", description = "升级aipp")
    public Rsp<AippCreateDto> upgradeAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam("version") String baselineVersion, @RequestBody AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.aippFlowService.upgrade(baselineVersion, aippDto, contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询aipp历史版本列表
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param httpRequest 操作上下文
     * @return aipp历史版本列表
     */
    @GetMapping(value = "/{aipp_id}/version", description = "查询aipp历史版本列表")
    public Rsp<List<AippVersionDto>> listAippVersion(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId) {
        return Rsp.ok(this.aippFlowService.listAippVersions(aippId, contextOf(httpRequest, tenantId)));
    }
}
