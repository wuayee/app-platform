package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.taskcenter.domain.OperationRecordEntity;
import com.huawei.fit.jober.taskcenter.service.OperationRecordService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.Map;

/**
 * 为操作记录提供 REST 风格 API。
 *
 * @author yWX1299574
 * @since 2023-12-29
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/operation-record", group = "操作记录管理接口")
public class OperationRecordController extends AbstractController {
    private final OperationRecordService service;

    public OperationRecordController(Authenticator authenticator, OperationRecordService service) {
        super(authenticator);
        this.service = service;
    }

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
        RangedResultSet<OperationRecordEntity> list = service.list(Views.filterOfOperationRecord(httpRequest), offset,
                limit, this.contextOf(httpRequest, tenantId));

        return Views.viewOf(list, "operationRecords", Views::viewOf);
    }
}
