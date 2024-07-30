/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.FitableInfoDto;
import com.huawei.fit.jober.aipp.service.GenericableManageService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Genericable相关接口
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-24
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/genericables", group = "fitable管理接口")
public class GenericableController extends AbstractController {
    private final GenericableManageService genericableManageService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param genericableManageService GenericableManageService实例
     */
    public GenericableController(Authenticator authenticator, GenericableManageService genericableManageService) {
        super(authenticator);
        this.genericableManageService = genericableManageService;
    }

    /**
     * 根据genericable id查询所有fitables
     *
     * @param httpRequest 请求对象
     * @param tenantId 租户id
     * @param genericableId genericable id
     * @param pageNum 分页页码
     * @param pageSize 分页大小
     * @return 返回fitables列表
     */
    @GetMapping(path = "/{genericable_id}", description = "查询指定genericable id下的所有fitables")
    public Rsp<List<FitableInfoDto>> getFitablesByGid(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("genericable_id") String genericableId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Rsp.ok(this.genericableManageService.getFitablesByGenerableId(genericableId, pageNum, pageSize));
    }

    /**
     * 调用灵感大全fitable
     *
     * @param httpRequest 请求对象
     * @param tenantId 租户id
     * @param fitableId fitable id
     * @param body 请求体，包含appId和appType
     * @return 返回执行结果
     */
    @PostMapping(path = "/fitables/{fitable_id}", description = "调用灵感大全fitable")
    public Rsp<List<Map<String, Object>>> executeInspirationFitable(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("fitable_id") String fitableId,
            @RequestBody Map<String, Object> body) {
        String appId = ObjectUtils.cast(body.get("appId"));
        String appType = ObjectUtils.cast(body.get("appType"));
        return Rsp.ok(this.genericableManageService.executeInspirationFitable(fitableId,
                appId,
                appType,
                this.contextOf(httpRequest, tenantId)));
    }
}
