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
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.AippInstanceQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.AippInstanceDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAippCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppStartDto;
import com.huawei.fit.jober.aipp.dto.form.AippFormRsp;
import com.huawei.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.runtime.entity.RuntimeData;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.Validated;

import java.util.List;
import java.util.Map;

/**
 * Aipp运行时管理接口
 *
 * @author l00611472
 * @since 2023-12-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "aipp运行时管理接口")
public class AppRunTimeController extends AbstractController {
    private final AippRunTimeService aippRunTimeService;
    private final com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeGenericable;
    private final AippFlowRuntimeInfoService aippFlowRuntimeInfoService;

    public AppRunTimeController(Authenticator authenticator, AippRunTimeService aippRunTimeService,
            com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeGenericable,
            AippFlowRuntimeInfoService aippFlowRuntimeInfoService) {
        super(authenticator);
        this.aippRunTimeService = aippRunTimeService;
        this.aippRunTimeGenericable = aippRunTimeGenericable;
        this.aippFlowRuntimeInfoService = aippFlowRuntimeInfoService;
    }

    /**
     * 查询aippd的node节点对应的表单
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param startOrEnd 开始或结束节点信息
     * @return 表单信息
     */
    @GetMapping(value = "/aipp/{aipp_id}/form-metadata/{edge}", description = "根据AippMetaId和node_index查询datasheet")
    public Rsp<AippFormRsp> queryEdgeSheetData(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam(value = "version", required = false) String version,
            @Property(description = "edge表示端侧节点，取值为start或者end", example = "start") @PathVariable("edge")
                    String startOrEnd) {
        return Rsp.ok(aippRunTimeService.queryEdgeSheetData(aippId,
                version,
                startOrEnd,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 创建分享
     *
     * @param queries 问答对
     * @return 分享唯一标识
     */
    @PostMapping(value = "/share", description = "分享对话")
    public Map<String, Object> shared(@RequestBody List<Map<String, Object>> queries) {
        return this.aippRunTimeService.shared(queries);
    }

    /**
     * 获取分享内容
     *
     * @param shareId 分享唯一标识
     * @return 分享内容
     */
    @GetMapping(value = "/share/{id}", description = "分享对话")
    public Map<String, Object> get(@PathVariable("id") String shareId) {
        return this.aippRunTimeService.getShareData(shareId);
    }

    /**
     * 启动一个Aipp
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @return 实例id
     */
    @PostMapping(path = "/aipp/{aipp_id}", description = "启动一个Aipp")
    public Rsp<String> createAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @Property(description = "initContext表示start表单填充的内容，作为流程初始化的businessData", example = "图片url, 文本输入, prompt")
            @RequestBody Map<String, Object> initContext, @RequestParam(value = "version") String version) {
        return Rsp.ok(this.aippRunTimeGenericable.createAippInstance(aippId,
                version,
                initContext,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除应用实例
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param instanceId 实例id
     * @return void
     */
    @DeleteMapping(path = "/aipp/{aipp_id}/instances/{instance_id}", description = "删除应用实例")
    public Rsp<Void> deleteInstance(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") String aippId, @PathVariable("instance_id") String instanceId,
            @RequestParam(value = "version") String version) {
        aippRunTimeService.deleteAippInstance(aippId, version, instanceId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 查询单个应用实例信息
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param instanceId 实例id
     * @return AIPP 实例
     */
    @GetMapping(path = "/aipp/{aipp_id}/instances/{instance_id}", description = "查询单个应用实例信息，实例运行期间前端定时调用")
    public Rsp<AippInstanceDto> getInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @PathVariable("instance_id") String instanceId, @RequestParam(value = "version") String version) {
        return Rsp.ok(aippRunTimeService.getInstance(aippId,
                version,
                instanceId,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 更新表单数据并上传到小海
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @return {@link Rsp}{@code <}{@link Void}{@code >}
     */
    @PutMapping(path = "/aipp/{aipp_id}/instances/{instance_id}/form", description = "更新表单数据并上传到小海")
    public Rsp<Void> updateAndUploadAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @PathVariable("instance_id") String instanceId,
            @Property(description = "用户填写的表单信息", example = "用户选择的大模型信息") @RequestBody Map<String, Object> formArgs) {
        aippRunTimeService.updateAndUploadAippInstance(aippId,
                instanceId,
                formArgs,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param aippId aippId
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @return
     */
    @PutMapping(path = "/aipp/{aipp_id}/instances/{instance_id}", description = "更新表单数据，并恢复实例任务执行")
    public Rsp<Void> resumeAndUpdateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @PathVariable("instance_id") String instanceId,
            @Property(description = "用户填写的表单信息", example = "用户选择的大模型信息") @RequestBody Map<String, Object> formArgs,
            @RequestParam(value = "version") String version) {
        aippRunTimeService.resumeAndUpdateAippInstance(aippId,
                version,
                instanceId,
                formArgs,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 终止实例任务
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户id
     * @param instanceId 实例id
     * @return
     */
    @PutMapping(path = "/instances/{instance_id}/terminate", description = "终止实例任务")
    public Rsp<Void> terminateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("instance_id") String instanceId) {
        this.aippRunTimeService.terminateInstance(instanceId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 批量查询实例列表
     *
     * @param httpRequest 操作上下文
     * @param tenantId 租户ID
     * @param cond 查询条件
     * @param page 分页条件
     * @return
     */
    @GetMapping(path = "/aipp/{aipp_id}/instances", description = "批量查询实例列表")
    public Rsp<PageResponse<AippInstanceDto>> getInstanceList(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestBean AippInstanceQueryCondition cond, @RequestBean PaginationCondition page,
            @RequestParam(value = "version") String version) {
        return Rsp.ok(aippRunTimeService.listInstance(aippId,
                version,
                cond,
                page,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 启动对话实例
     *
     * @param httpRequest 操作上下文
     * @param tenantId    租户id
     * @param appBuilderAippCreateDto 启动对话结构体
     * @return 实例id
     */
    @PostMapping(path = "/aipp/{app_id}/start", description = "启动一个对话实例")
    public Rsp<AppBuilderAppStartDto> startInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @Property(description = "initContext表示start表单填充的内容，作为流程初始化的businessData",
                    example = "图片url, 文本输入, prompt")
            @RequestBody @Validated AppBuilderAippCreateDto appBuilderAippCreateDto) {
        return Rsp.ok(aippRunTimeService.startInstance(
                appBuilderAippCreateDto.getAppDto(),
                appBuilderAippCreateDto.getContext(),
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询流程运行时数据.
     *
     * @param httpRequest 操作上下文
     * @param aippId 应用id.
     * @param instanceId 实例id.
     * @return {@link Rsp}{@code <}{@link List}{@code <}{@link RuntimeData}{@code >}{@code >} 运行时数据.
     */
    @GetMapping(value = "/aipp/{aipp_id}/instances/{instance_id}/runtime", description = "查询流程运行时信息")
    public Rsp<RuntimeData> getRuntimeInfo(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @PathVariable("instance_id") String instanceId, @RequestParam(value = "version") String version) {
        OperationContext ctx = this.contextOf(httpRequest, tenantId);
        RuntimeData runtimeData = this.aippFlowRuntimeInfoService.getRuntimeData(aippId, version, instanceId, ctx)
                .orElse(null);
        return Rsp.ok(runtimeData);
    }
}
