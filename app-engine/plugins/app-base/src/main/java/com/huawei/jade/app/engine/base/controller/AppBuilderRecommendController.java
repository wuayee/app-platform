/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.base.controller;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.base.dto.AppBuilderRecommendDto;
import com.huawei.jade.app.engine.base.service.AppBuilderRecommendService;

import java.util.List;

/**
 * 猜你想问获取接口
 *
 * @author y00858250
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
        String question = recommendDto.getQuestion();
        String answer = recommendDto.getAnswer();

        return Rsp.ok(recommendService.queryRecommends(question, answer, recommendDto.getModel()));
    }
}
