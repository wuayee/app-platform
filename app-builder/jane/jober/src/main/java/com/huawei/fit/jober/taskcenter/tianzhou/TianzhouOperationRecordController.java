/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.taskcenter.controller.OperationRecordController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 为操作记录提供 REST 风格 API。
 *
 * @author yWX1299574
 * @since 2024-01-03
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/operation-record", group = "天舟操作记录管理接口")
@RequiredArgsConstructor
public class TianzhouOperationRecordController extends TianzhouAbstractController {
    private final OperationRecordController controller;

    private final Plugin plugin;

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询操作记录列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        return View.viewOf(() -> controller.list(httpRequest, httpResponse, tenantId, offset, limit), plugin,
                httpRequest);
    }
}
