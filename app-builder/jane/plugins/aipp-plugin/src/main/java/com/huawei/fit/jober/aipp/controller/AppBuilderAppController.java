/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.PutMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app")
public class AppBuilderAppController extends AbstractController {
    private final AppBuilderAppService appService;

    public AppBuilderAppController(Authenticator authenticator, AppBuilderAppService appService) {
        super(authenticator);
        this.appService = appService;
    }

    @GetMapping(description = "查询 app 列表")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return this.appService.list(httpRequest, tenantId, offset, limit);
    }

    @GetMapping(value = "/{app_id}", description = "查询 app ")
    public Rsp<AppBuilderAppDto> query(HttpClassicServerRequest httpRequest, @PathVariable("app_id") String appId) {
        return this.appService.query(httpRequest, appId);
    }

    @PostMapping(value = "/{app_id}", description = "根据模板创建aipp")
    public Rsp<AppBuilderAppDto> create(HttpClassicServerRequest request, @PathVariable("app_id") String appId,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AppBuilderAppCreateDto dto) {
        return this.appService.create(appId, dto, this.contextOf(request, tenantId));
    }

    @PutMapping(value = "/{app_id}/config", description = "通过config更新aipp")
    public Rsp<AppBuilderAppDto> updateByConfig(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated AppBuilderConfigDto configDto) {
        return this.appService.updateConfig(appId, configDto, this.contextOf(httpRequest, tenantId));
    }

    @PutMapping(value = "/{app_id}/graph", description = "根据graph更新aipp")
    public Rsp<AppBuilderAppDto> updateByGraph(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated AppBuilderFlowGraphDto flowGraphDto) {
        return this.appService.updateFlowGraph(appId, flowGraphDto, this.contextOf(httpRequest, tenantId));
    }

    @PutMapping(value = "/{app_id}", description = "创建aipp")
    public Rsp<AppBuilderAppDto> update(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated AppBuilderAppDto appDto) {
        return this.appService.updateApp(appId, appDto, this.contextOf(httpRequest, tenantId));
    }

    // todo 返回值暂时使用 aipp 运行态返回值
    @PostMapping(path = "/{app_id}/publish", description = "发布 app ")
    public Rsp<AippCreateDto> publish(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated AppBuilderAppDto appDto) {
        return this.appService.publish(appDto, this.contextOf(httpRequest, tenantId));
    }

    @PostMapping(path = "/{app_id}/debug", description = "调试 app ")
    public Rsp<AippCreateDto> debug(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated AppBuilderAppDto appDto) {
        return this.appService.debug(appDto, this.contextOf(httpRequest, tenantId));
    }

    @PostMapping(path = "/{app_id}/inspiration/department", description = "获取灵感大全的部门信息")
    public Rsp<AippCreateDto> inspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AppBuilderAppDto appDto) {
        throw new UnsupportedOperationException();
    }
}
