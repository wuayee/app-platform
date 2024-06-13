/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
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

    @GetMapping(path = "/nodes", description = "获取所有工具和基础节点配置")
    public Rsp<StoreNodeConfigResDto> getBasicNodesAndTools(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "tag") String tag) {
        return Rsp.ok(this.storeService.getBasicNodesAndTools(tag, pageNum, pageSize));
    }

    @GetMapping(path = "/models", description = "获取任务的模型列表")
    public Rsp<List<String>> getModels(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "taskName", defaultValue = "") String taskName) {
        return Rsp.ok(this.storeService.getModels(taskName, pageNum, pageSize));
    }

    @GetMapping(path = "/waterflow", description = "获取所有工具流")
    public Rsp<List<AppBuilderWaterFlowInfoDto>> getWaterFlowInfos(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Rsp.ok(this.storeService.getWaterFlowInfos(pageNum, pageSize));
    }
}
