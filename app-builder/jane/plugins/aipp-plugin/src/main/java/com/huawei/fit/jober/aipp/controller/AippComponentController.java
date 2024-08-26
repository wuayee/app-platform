/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AippFlowComponentDto;
import com.huawei.fit.jober.aipp.dto.AippFormComponentDto;
import com.huawei.fit.jober.aipp.init.AippComponentInitiator;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;

/**
 * 组件查询接口
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/aipp-component", group = "aipp组件管理接口")
public class AippComponentController {
    /**
     * 查询表单组件
     *
     * @param tenantId tenantId
     * @return Rsp<AippFlowComponentDto>
     */
    @GetMapping(value = "/form", description = "获取表单的组件列表")
    public Rsp<AippFormComponentDto> queryFormComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(AippComponentInitiator.getLocaleObject(AippConst.FORM_COMPONENT_DATA_EN_KEY,
                AippConst.FORM_COMPONENT_DATA_ZH_KEY,
                AippFormComponentDto.class));
    }

    /**
     * 查询流程组件
     *
     * @param tenantId tenantId
     * @return Rsp<AippFlowComponentDto>
     */
    @GetMapping(value = "/flow", description = "获取flow的组件列表")
    public Rsp<AippFlowComponentDto> queryFlowComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(AippComponentInitiator.getLocaleObject(AippConst.FLOW_COMPONENT_DATA_EN_KEY,
                AippConst.FLOW_COMPONENT_DATA_ZH_KEY,
                AippFlowComponentDto.class));
    }
}
