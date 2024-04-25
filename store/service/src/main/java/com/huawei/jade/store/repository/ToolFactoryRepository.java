/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository;

import com.huawei.jade.store.ToolFactory;

/**
 * 表示创建工具的工厂接口。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
public interface ToolFactoryRepository {
    /**
     * 注册工具工厂。
     *
     * @param factory 表示注册的工具工厂的 {@link ToolFactory}。
     */
    void register(ToolFactory factory);

    /**
     * 反注册工具工厂。
     *
     * @param type 表示注册的工具工厂的 {@link String}。
     */
    void unregister(String type);

    /**
     * 根据类型查询工厂。
     *
     * @param type 表示工厂的类型的 {@link String}。
     * @return 表示返回的工厂的 {@link ToolFactory}。
     */
    ToolFactory query(String type);
}
