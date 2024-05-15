/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Component;

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
    public Rsp<StoreNodeConfigResDto> getBasicNodesAndTools() {
        return Rsp.ok(this.storeService.getBasicNodesAndTools());
    }
}
