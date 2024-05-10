/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.controller.a3000.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 清洗任务结果
 *
 * @author y00679285
 * @since 2024/2/2
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CleanTaskPageResult {
    /**
     * 总数量
     */
    private int totalNum;

    /**
     * 查询结果列表
     */
    private List<Map<String, Object>> result;
}
