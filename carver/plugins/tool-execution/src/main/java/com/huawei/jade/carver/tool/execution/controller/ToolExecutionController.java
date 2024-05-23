/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.execution.controller;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.carver.tool.service.ToolExecuteService;

/**
 * 表示对外提供工具执行的 HTTP 调用接口。
 *
 * @author 王攀博
 * @since 2024-04-29
 */
@Component
public class ToolExecutionController {
    private final ToolExecuteService toolExecuteService;

    ToolExecutionController(ToolExecuteService toolExecuteService) {
        this.toolExecuteService = notNull(toolExecuteService, "The execution service cannot be null.");
    }

    /**
     * 执行工具的请求。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param uniqueName 表示工具唯一名称的 {@link String}。
     * @param jsonArgs 表示工具执行参数作为消息体传入的 {@link String}。
     * @return 表示工具执行结果的 {@link String}。
     */
    @PostMapping(path = "/store/platform/{platform}/exec")
    public String toolExecute(@PathVariable("platform") String platform, @RequestParam("uniqueName") String uniqueName,
            @RequestBody String jsonArgs) {
        return this.toolExecuteService.executeTool(uniqueName, jsonArgs);
    }
}