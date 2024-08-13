/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.controller;

import com.huawei.fit.waterflow.edatamate.entity.CleanTaskPageResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public final class Views {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Views() {
    }

    /**
     * viewOf
     *
     * @param cleanTaskPageResult 分页清洗结果
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(CleanTaskPageResult cleanTaskPageResult) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put("totalPage", cleanTaskPageResult.getTotalNum());
        view.put("result", cleanTaskPageResult.getResult());
        return view;
    }
}
