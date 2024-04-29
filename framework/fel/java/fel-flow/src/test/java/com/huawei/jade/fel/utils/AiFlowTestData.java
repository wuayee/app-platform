/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于测试的数据结构。
 *
 * @author 刘信宏
 * @since 2024-04-29
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AiFlowTestData {
    private int first = 0;
    private int second = 0;

    /**
     * 获取总数。
     *
     * @return 返回元素总和的 {@code int}。
     */
    public int total() {
        return first + second;
    }
}
