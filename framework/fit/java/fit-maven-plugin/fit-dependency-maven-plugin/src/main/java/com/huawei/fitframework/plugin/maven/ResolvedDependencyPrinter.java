/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven;

/**
 * {@link ResolvedDependencyPrinter} 的接口声明。
 *
 * @author 梁济时 00298979
 * @since 2020-10-09
 */
public interface ResolvedDependencyPrinter {
    void print(ResolvedDependency dependency);
}
