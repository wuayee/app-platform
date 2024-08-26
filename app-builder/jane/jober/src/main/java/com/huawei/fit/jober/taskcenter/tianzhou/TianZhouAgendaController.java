/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.taskcenter.controller.AgendaController;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import java.util.Map;

/**
 * 模板个人待办相关controller
 *
 * @author 罗书强
 * @since 2024-01-29
 */
@Component
@RequestMapping(value = "/v1/jane/task-templates/{task_template_id}/instances", group = "天舟个人待办管理接口")
@RequiredArgsConstructor
public class TianZhouAgendaController {
    private final Plugin plugin;

    private final AgendaController agendaController;

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param offset offset
     * @param limit limit
     * @param deleted deleted
     * @param templateId templateId
     * @return Map<String, Object>
     */
    @GetMapping(summary = "根据模板和筛选条件查询对应待办")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listAgenda(HttpClassicServerRequest httpRequest, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit, @RequestParam(name = "deleted", required = false) String deleted,
            @PathVariable("task_template_id") String templateId) {
        return View.viewOf(() -> agendaController.listAgenda(httpRequest, offset, limit, deleted, templateId), plugin,
                httpRequest);
    }
}
