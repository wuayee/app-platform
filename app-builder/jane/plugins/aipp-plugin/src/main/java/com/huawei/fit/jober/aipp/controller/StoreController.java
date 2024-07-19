/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.ModelDto;
import com.huawei.fit.jober.aipp.dto.StoreBasicNodeInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.dto.ToolDto;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;

/**
 * Store 相关接口
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/store")
public class StoreController extends AbstractController {
    private final StoreService storeService;

    public StoreController(Authenticator authenticator, StoreService storeService) {
        super(authenticator);
        this.storeService = storeService;
    }

    /**
     * 获取所有工具和基础节点配置
     *
     * @param httpRequest 请求
     * @param canOrTags tags的拼接方式
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @param tag 标签
     * @param version 版本
     * @return 查询结果
     *
     * @deprecated
     */
    @Deprecated
    @GetMapping(path = "/nodes", description = "获取所有工具和基础节点配置")
    public Rsp<StoreNodeConfigResDto> getBasicNodesAndTools(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "orTags", defaultValue = "false", required = false) boolean canOrTags,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "tag") String tag,
            @RequestParam(value = "version") String version) {
        return Rsp.ok(this.storeService.getBasicNodesAndTools(tag, canOrTags, pageNum, pageSize, version));
    }

    /**
     * 获取任务的模型列表。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param taskName 表示任务名的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Rsp}{@code <}{@link ModelDto}{@code >}。
     */
    @GetMapping(path = "/models", description = "获取任务的模型列表")
    public Rsp<ModelDto> getModels(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "taskName", defaultValue = "") String taskName) {
        return Rsp.ok(this.storeService.getModels(taskName, pageNum, pageSize));
    }

    /**
     * 获取已发布的所有指定类型的插件配置
     *
     * @param httpRequest 请求
     * @param tag 标签
     * @param tenantId 租户Id
     * @param canOrTags tag拼接方式
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @return 结果
     */
    @GetMapping(path = "/plugins", description = "获取已发布的所有指定类型的插件配置")
    public Rsp<ToolDto> getPlugins(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "tag", defaultValue = "", required = false) String tag,
            @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "orTags", defaultValue = "false", required = false) boolean canOrTags,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Rsp.ok(this.storeService.getPlugins(tag, canOrTags, pageNum, pageSize,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 获取基础节点配置
     *
     * @param httpRequest 请求
     * @return 结果
     */
    @GetMapping(path = "/nodes/basic", description = "获取基础节点配置")
    public Rsp<List<StoreBasicNodeInfoDto>> getBasic(HttpClassicServerRequest httpRequest) {
        return Rsp.ok(this.storeService.getBasic());
    }

    /**
     * 获取所有工具流
     *
     * @param httpRequest 请求
     * @param canOrTags tags的拼接方式
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @param version 版本
     * @return 查询结果
     */
    @GetMapping(path = "/waterflow", description = "获取所有工具流")
    public Rsp<List<AppBuilderWaterFlowInfoDto>> getWaterFlowInfos(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "orTags", defaultValue = "false", required = false) boolean canOrTags,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "version") String version) {
        return Rsp.ok(this.storeService.getWaterFlowInfos(canOrTags, pageNum, pageSize, version));
    }
}
