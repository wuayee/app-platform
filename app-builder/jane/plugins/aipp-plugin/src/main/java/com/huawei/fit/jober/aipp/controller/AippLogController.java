/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;

/**
 * aipp实例log管理接口
 *
 * @author l00611472
 * @since 2024-01-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/log", group = "aipp实例log管理接口")
public class AippLogController extends AbstractController {
    private final AippLogService aippLogService;

    public AippLogController(Authenticator authenticator, AippLogService aippLogService) {
        super(authenticator);
        this.aippLogService = aippLogService;
    }

    @GetMapping(path = "/app/{app_id}/recent", description = "指定appId查询实例历史记录（查询最新5个实例）")
    public Rsp<List<AippInstLogDataDto>> queryRecentInstanceLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam("type") String type) {
        return Rsp.ok(this.aippLogService.queryAippRecentInstLog(appId, type, this.contextOf(httpRequest, tenantId)));
    }

    @DeleteMapping(path = "/app/{app_id}", description = "清除appId查询实例的全部历史记录")
    public Rsp<Void> deleteInstanceLog(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") String appId, @RequestParam("type") String type) {
        this.aippLogService.deleteAippInstLog(appId, type, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    @GetMapping(path = "/instance/{instance_id}", description = "指定instanceId条件查询实例记录")
    public Rsp<List<AippInstLog>> queryInstanceSince(@PathVariable("instance_id") String instanceId,
            @RequestParam(name = "after_at", required = false) String sinceTime) {
        return Rsp.ok(aippLogService.queryInstanceLogSince(instanceId, sinceTime));
    }

    /**
     * 根据appId查询应用历史记录
     *
     * @param httpRequest Http请求体
     * @param tenantId 租户id
     * @param appId 应用id
     * @param type 应用类型
     * @return Rsp<List<AippInstLogDataDto>> 应用历史记录
     */
    @GetMapping(path = "/app/{app_id}/chat/recent", description = "指定appId查询实例历史记录（查询最新1个会话）")
    public Rsp<List<AippInstLogDataDto>> queryRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam("type") String type) {
        return Rsp.ok(this.aippLogService.queryAppRecentChatLog(appId, type, this.contextOf(httpRequest, tenantId)));
    }
}
