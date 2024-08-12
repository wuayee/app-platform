/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jober.taskcenter.controller.GenericableController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Genericable相关Controller。
 *
 * @author 陈镕希
 * @since 2023-07-14
 */
@Component
@RequiredArgsConstructor
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/genericables", group = "天舟Genericable管理接口")
public class TianzhouGenericableController extends TianzhouAbstractController {
    private final GenericableController genericableController;

    private final Plugin plugin;

    /**
     * 获取某个Genericable的实现列表信息。
     *
     * @param genericableId 需要查询的Genericable唯一标识的 {@link String}。
     * @param httpRequest 请求体
     * @return Genericable唯一标识对应Genericable实现列表的
     * {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    @GetMapping(path = "/{genericableId}", summary = "根据GenericableID获取对应的fitable实现列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> getGenericableImplementInfo(@PathVariable("genericableId") String genericableId,
            HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> genericableController.getGenericableImplementInfo(genericableId), plugin, httpRequest);
    }
}
