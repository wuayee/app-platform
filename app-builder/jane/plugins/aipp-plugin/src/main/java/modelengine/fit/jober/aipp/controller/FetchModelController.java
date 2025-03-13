/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fitframework.annotation.Component;

/**
 * app-engine 后端查询接入模型服务列表的接口。
 *
 * @author 方誉州
 * @since 2024-09-14
 */
@Component
@RequestMapping(path = "/v1/api/fetch/model-list")
public class FetchModelController {
    private final AippModelCenter aippModelCenter;

    FetchModelController(AippModelCenter aippModelCenter) {
        this.aippModelCenter = aippModelCenter;
    }

    /**
     * 查询接入的模型服务列表。
     *
     * @return 表示模型列表信息的 {@link ModelListDto}。
     */
    @GetMapping()
    public ModelListDto fetchModelList(@RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "scene", required = false) String scene) {
        return this.aippModelCenter.fetchModelList(type, scene);
    }
}
