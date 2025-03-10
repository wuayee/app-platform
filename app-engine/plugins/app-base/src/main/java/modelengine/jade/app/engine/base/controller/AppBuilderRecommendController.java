/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.controller;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.dto.AppBuilderRecommendDto;
import modelengine.jade.app.engine.base.service.AppBuilderRecommendService;

import java.util.List;

/**
 * 猜你想问获取接口
 *
 * @author 杨海波
 * @since 2024-05-24
 */
@Component
@RequestMapping(path = "/v1/api/recommend")
public class AppBuilderRecommendController {
    private final AppBuilderRecommendService recommendService;

    public AppBuilderRecommendController(AppBuilderRecommendService recommendService) {
        this.recommendService = recommendService;
    }

    /**
     * 获取猜你想问推荐列表
     *
     * @param recommendDto 包含上次对话用户提问及模型回答
     * @return 三个推荐问题列表
     */
    @PostMapping
    public Rsp<List<String>> queryRecommends(@RequestBody AppBuilderRecommendDto recommendDto) {
        return Rsp.ok(recommendService.queryRecommends(recommendDto));
    }
}
