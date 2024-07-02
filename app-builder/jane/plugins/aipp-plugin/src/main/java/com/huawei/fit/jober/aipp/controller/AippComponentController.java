/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import static com.huawei.fit.jober.aipp.init.AippComponentInitiator.COMPONENT_DATA;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AippFlowComponentDto;
import com.huawei.fit.jober.aipp.dto.AippFormComponentDto;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;

/**
 * 组件查询接口
 *
 * @author x00576283
 * @since 2023/12/11
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/aipp-component", group = "aipp组件管理接口")
public class AippComponentController {
    @GetMapping(value = "/form", description = "获取表单的组件列表")
    public Rsp<AippFormComponentDto> queryFormComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(JsonUtils.parseObject(COMPONENT_DATA.get(AippConst.FORM_COMPONENT_DATA_KEY),
                AippFormComponentDto.class));
    }

    @GetMapping(value = "/flow", description = "获取flow的组件列表")
    public Rsp<AippFlowComponentDto> queryFlowComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(JsonUtils.parseObject(COMPONENT_DATA.get(AippConst.FLOW_COMPONENT_DATA_KEY),
                AippFlowComponentDto.class));
    }
}
