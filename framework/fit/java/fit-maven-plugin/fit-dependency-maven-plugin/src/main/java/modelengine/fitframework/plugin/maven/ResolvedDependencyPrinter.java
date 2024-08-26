/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package modelengine.fitframework.plugin.maven;

/**
 * {@link ResolvedDependencyPrinter} 的接口声明。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
public interface ResolvedDependencyPrinter {
    /**
     * 打印解析后的依赖信息。
     *
     * @param dependency 表示依赖信息的 {@link ResolvedDependencyPrinter}。
     */
    void print(ResolvedDependency dependency);
}
