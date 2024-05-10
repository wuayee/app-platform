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

    public GenericableController(Authenticator authenticator, GenericableManageService genericableManageService) {
        super(authenticator);
        this.genericableManageService = genericableManageService;
    }

    @GetMapping(path = "/{genericable_id}", description = "查询指定genericable id下的所有fitables")
    public Rsp<List<FitableInfoDto>> getFitablesByGid(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("genericable_id") String genericableId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Rsp.ok(this.genericableManageService.getFitablesByGenerableId(genericableId, pageNum, pageSize));
    }

    @PostMapping(path = "/fitables/{fitable_id}", description = "调用灵感大全fitable")
    public Rsp<List<Map<String, Object>>> executeInspirationFitable(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("fitable_id") String fitableId,
            @RequestBody Map<String, Object> body) {
        String appId = (String) body.get("appId");
        String appType = (String) body.get("appType");
        return Rsp.ok(this.genericableManageService.executeInspirationFitable(fitableId,
                appId,
                appType,
                this.contextOf(httpRequest, tenantId)));
    }
}
