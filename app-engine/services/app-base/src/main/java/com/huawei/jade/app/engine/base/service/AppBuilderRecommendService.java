/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service;

import java.util.List;

/**
 * 猜你想问的查询接口service
 *
 * @author 杨海波
 * @since 2024-05-24
 */
public interface AppBuilderRecommendService {
    /**
     * queryRecommends猜你想问推荐问题查询
     *
     * @param ques 前次用户提问
     * @param ans 前次模型回答
     * @param model 大模型名称
     * @return List<String> 3个推荐问题列表
     */
    List<String> queryRecommends(String ques, String ans, String model);
}
