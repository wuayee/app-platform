/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.aipp.dto.PublishedAppResDto;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.aipp.util.ConvertUtils;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.validation.Validated;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;

/**
 * aipp的CRUD接口。
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app")
public class AppBuilderAppController extends AbstractController {
    private final List<String> excludeNames;
    private final AppBuilderAppService appService;
    private final com.huawei.fit.jober.aipp.genericable.AppBuilderAppService appGenericable;

    /**
     * 构造函数。
     *
     * @param authenticator 认证器。
     * @param appService app服务。
     * @param appGenericable app通用服务。
     * @param excludeNames 排除名称列表。
     */
    public AppBuilderAppController(Authenticator authenticator, AppBuilderAppService appService,
            com.huawei.fit.jober.aipp.genericable.AppBuilderAppService appGenericable,
            @Value("${app-engine.exclude-names}") List<String> excludeNames) {
        super(authenticator);
        this.excludeNames = excludeNames;
        this.appService = appService;
        this.appGenericable = appGenericable;
    }

    /**
     * 查询 app 列表。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param offset 偏移量。
     * @param limit 每页查询条数。
     * @param cond 查询条件。
     * @param type 查询类型。
     * @return 查询结果列表。
     */
    @GetMapping(description = "查询 app 列表")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit, @RequestBean AppQueryCondition cond,
            @RequestQuery(name = "type", defaultValue = "app") String type) {
        cond.setType(type);
        cond.getExcludeNames().addAll(this.excludeNames);
        return this.appService.list(cond, httpRequest, tenantId, offset, limit);
    }

    /**
     * 查询单个app。
     *
     * @param appId 待查询app的Id。
     * @return 查询结果。
     */
    @GetMapping(value = "/{app_id}", description = "查询 app ")
    public Rsp<AppBuilderAppDto> query(@PathVariable("app_id") String appId) {
        return Rsp.ok(this.appGenericable.query(appId));
    }

    /**
     * 查询 app 最新可编排的版本。
     *
     * @param httpRequest 请求参数。
     * @param tenantId 租户Id。
     * @param appId 表示待查询app的Id的 {@link String}。
     * @return 表示查询app的最新可编排版本的DTO {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{app_id}/latest_orchestration", description = "查询 app 最新可编排的版本")
    public Rsp<AppBuilderAppDto> queryLatestOrchestration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return Rsp.ok(this.appGenericable.queryLatestOrchestration(appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询 app 的历史发布版本。
     *
     * @param httpRequest 请求参数。
     * @param appId 查询app的Id。
     * @param tenantId 租户Id。
     * @param offset 偏移量。
     * @param limit 查询条数。
     * @param cond 条件。
     * @return 查询结果列表。
     */
    @GetMapping(value = "/{app_id}/recentPublished", description = "查询 app 的历史发布版本")
    public Rsp<List<PublishedAppResDto>> recentPublished(HttpClassicServerRequest httpRequest,
            @PathVariable("app_id") String appId, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit, @RequestBean AppQueryCondition cond) {
        return Rsp.ok(this.appService.recentPublished(cond,
                offset,
                limit,
                appId,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 获取应用的发布详情。
     *
     * @param httpRequest 请求参数。
     * @param tenantId 租户Id。
     * @param uniqueName 唯一名称。
     * @return 查询结果。
     */
    @GetMapping(path = "/published/unique_name/{unique_name}", description = "获取应用的发布详情")
    public Rsp<PublishedAppResDto> published(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("unique_name") String uniqueName) {
        return Rsp.ok(this.appService.published(uniqueName, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据模板创建aipp。
     *
     * @param request 请求参数。
     * @param appId 模板的Id。
     * @param tenantId 租户Id。
     * @param dto 创建参数。
     * @return 结果。
     */
    @WithSpan(value = "operation.appBuilderApp.template")
    @PostMapping(value = "/{app_id}", description = "根据模板创建aipp")
    public Rsp<AppBuilderAppDto> create(HttpClassicServerRequest request, @PathVariable("app_id") String appId,
            @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttribute("name:$.name") AppBuilderAppCreateDto dto) {
        return Rsp.ok(this.appService.create(appId, dto, this.contextOf(request, tenantId), false));
    }

    /**
     * 通过config更新aipp。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待更新的app的Id。
     * @param configDto 待更新的config的DTO。
     * @return 更新结果。
     */
    @WithSpan(value = "operation.appBuilderApp.config")
    @PutMapping(value = "/{app_id}/config", description = "通过config更新aipp")
    public Rsp<AppBuilderAppDto> updateByConfig(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated @SpanAttribute("name:$.name") AppBuilderConfigDto configDto) {
        return this.appService.updateConfig(appId, configDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 根据graph更新aipp。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId app的Id。
     * @param flowGraphDto graph的DTO。
     * @return 结果。
     */
    @WithSpan(value = "operation.appBuilderApp.graph")
    @PutMapping(value = "/{app_id}/graph", description = "根据graph更新aipp")
    public Rsp<AppBuilderAppDto> updateByGraph(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated @SpanAttribute("name:$.name") AppBuilderFlowGraphDto flowGraphDto) {
        return this.appService.updateFlowGraph(appId, flowGraphDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 更新app的基本信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId appId。
     * @param appDto 基本信息。
     * @return 结果。
     */
    @WithSpan(value = "operation.appBuilderApp.update")
    @PutMapping(value = "/{app_id}", description = "更新 app")
    public Rsp<AppBuilderAppDto> update(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated @SpanAttribute("name:$.name") AppBuilderAppDto appDto) {
        return this.appService.updateApp(appId, appDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 发布 app。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的信息。
     * @return 暂时使用 aipp 运行态返回值。
     */
    @WithSpan(value = "operation.appBuilderApp.publish")
    @PostMapping(path = "/{app_id}/publish", description = "发布 app ")
    public Rsp<AippCreateDto> publish(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttribute("name:$.name, version:$.version") AppBuilderAppDto appDto) {
        return this.appService.publish(appDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 调试App。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的信息。
     * @return 结果。
     */
    @WithSpan(value = "operation.appBuilderApp.debug")
    @PostMapping(path = "/{app_id}/debug", description = "调试 app ")
    public Rsp<AippCreateDto> debug(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttribute("name:$.name") AppBuilderAppDto appDto) {
        return Rsp.ok(ConvertUtils.toAippCreateDto(this.appGenericable.debug(appDto,
                this.contextOf(httpRequest, tenantId))));
    }

    /**
     * 获取 app 最新发布版本信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待请求的appId。
     * @return 返回结果。
     */
    @GetMapping(path = "/{app_id}/latest_published", description = "获取 app 最新发布版本信息")
    public Rsp<AippCreateDto> latestPublished(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return Rsp.ok(ConvertUtils.toAippCreateDto(this.appGenericable.queryLatestPublished(appId,
                this.contextOf(httpRequest, tenantId))));
    }

    /**
     * 获取灵感大全的部门信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的DTO。
     * @return 结果。
     */
    @WithSpan(value = "operation.appBuilderApp.idea")
    @PostMapping(path = "/{app_id}/inspiration/department", description = "获取灵感大全的部门信息")
    public Rsp<AippCreateDto> inspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AppBuilderAppDto appDto) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除 app。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待删除的appId。
     * @return 空结果。
     */
    @WithSpan(value = "operation.appBuilderApp.delete")
    @DeleteMapping(path = "/{app_id}", description = "删除 app")
    public Rsp<Void> delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") @SpanAttribute("app_id") String appId) {
        this.appService.delete(appId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }
}
