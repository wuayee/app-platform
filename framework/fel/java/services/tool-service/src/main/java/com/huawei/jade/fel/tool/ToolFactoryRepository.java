/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import java.util.Optional;
import java.util.Set;

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
     * @param factory 表示要反注册的工具工厂的 {@link ToolFactory}。
     */
    void unregister(ToolFactory factory);

    /**
     * 根据运行规范标签匹配一个工厂。
     *
     * @param runnable 表示运行规范集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示返回的工厂的 {@link Optional}{@code <}{@link ToolFactory}{@code >}，否则为 {@link Optional#empty()}。
     */
    Optional<ToolFactory> match(Set<String> runnable);
}
