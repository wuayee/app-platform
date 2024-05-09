/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于测试的数据结构。
 *
 * @author 刘信宏
 * @since 2024-04-29
 */
@NoArgsConstructor
@Getter
public class AiFlowTestData {
    private final AtomicInteger first = new AtomicInteger(0);
    private final AtomicInteger second = new AtomicInteger(0);
    private final AtomicInteger third = new AtomicInteger(0);

    /**
     * 初始化 {@link AiFlowTestData}。
     *
     * @param first 表示第一个值的 {@code int}。
     * @param second 表示第二个值的 {@code int}。
     * @param third 表示第三个值的 {@code int}。
     */
    public AiFlowTestData(int first, int second, int third) {
        this.first.set(first);
        this.second.set(second);
        this.third.set(third);
    }

    /**
     * 获取总数。
     *
     * @return 返回元素总和的 {@code int}。
     */
    public int total() {
        return first.get() + second.get() + third.get();
    }
}
