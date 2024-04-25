/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.examples;

import com.huawei.jade.fel.core.examples.support.DefaultExampleSelector;

/**
 * 例子选择器接口定义。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public interface ExampleSelector {
    /**
     * 创建默认实现 {@link DefaultExampleSelector} 的构建器。
     *
     * @return 返回表示构建器实例的 {@link DefaultExampleSelector.Builder}。
     */
    static DefaultExampleSelector.Builder builder() {
        return new DefaultExampleSelector.Builder();
    }

    /**
     * 通过用户问题选择例子集合。
     *
     * @param question 表示用户问题的 {@link String}。
     * @return 返回表示例子集合的 {@link String}。
     */
    String select(String question);
}