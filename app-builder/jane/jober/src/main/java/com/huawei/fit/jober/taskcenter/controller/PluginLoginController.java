/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.jober.taskcenter.service.PluginLoginService;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.Map;

/**
 * 提供一个无鉴权的 cookie 存储和获取 API。
 *
 * @author 姚江
 * @since 2024-02-17
 */
@Component
@RequestMapping(value = "/v1/{client_id}/cookie", group = "cookie管理接口")
@RequiredArgsConstructor
public class PluginLoginController {
    private final PluginLoginService service;

    /**
     * cookie失效或者插件首次调用
     *
     * @param clientId 插件客户端Id
     */
    @DeleteMapping(summary = "使原本cookie失效")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(@PathVariable("client_id") String clientId) {
        service.delete(clientId);
    }

    /**
     * 保存新的cookie
     *
     * @param httpRequest http请求
     * @param clientId 插件客户端Id
     * @param request 存放cookie
     * @return 登录结果信息 {@link String}
     */
    @PostMapping(summary = "保存新的cookie")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public String save(HttpClassicServerRequest httpRequest, @PathVariable("client_id") String clientId,
            @RequestBody Map<String, String> request) {
        String cookie = httpRequest.headers().require("Cookie");
        return service.save(clientId, cookie);
    }

    /**
     * 获取cookie
     *
     * @param clientId 插件客户端Id
     * @return cookie信息
     */
    @GetMapping(summary = "获取cookie")
    @ResponseStatus(HttpResponseStatus.OK)
    public String getCookie(@PathVariable("client_id") String clientId) {
        return service.get(clientId);
    }
}