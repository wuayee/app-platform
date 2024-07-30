/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.taskcenter.controller.TaskRelationsController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 任务关联相关controller类
 *
 * @author 罗书强 lwx1291633
 * @since 2024-01-02
 */
@Component
@RequestMapping(path = "/v1/jane/relations", group = "天舟任务关联相关接口")
@RequiredArgsConstructor
public class TianZhouTaskRelationsController {
    private final TaskRelationsController taskRelationsController;

    private final Plugin plugin;

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建关联")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> taskRelationsController.create(httpRequest, httpResponse, request), plugin,
                httpRequest);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param relationId relationId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{relation_id}", summary = "根据关联ID删除关联关系")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("relation_id") String relationId) {
        return View.viewOf(() -> taskRelationsController.delete(httpRequest, httpResponse, relationId), plugin,
                httpRequest);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param relationId relationId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{relation_id}", summary = "根据关联ID获取关联任务信息")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("relation_id") String relationId) {
        return View.viewOf(() -> taskRelationsController.retrieve(httpRequest, httpResponse, relationId), plugin,
                httpRequest);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询关联任务列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        return View.viewOf(() -> taskRelationsController.list(httpRequest, offset, limit), plugin,
                httpRequest);
    }
}
