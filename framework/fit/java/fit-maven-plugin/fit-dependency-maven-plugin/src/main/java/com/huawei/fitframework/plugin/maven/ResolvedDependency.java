/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven;

import com.huawei.fitframework.plugin.maven.support.EmptyResolvedDependency;

import java.util.Set;

/**
 * @author 梁济时 00298979
 * @since 2020-10-09
 */
public interface ResolvedDependency {
    Set<MavenCoordinate> getMissingDependencies();

    Set<String> getMissingDependedClassNames(MavenCoordinate missingDependency);

    Set<String> getMissingDependencyUserClassNames(MavenCoordinate missingDependency, String dependedClassName);

    Set<MavenCoordinate> getRedundantDependencies();

    default void print(ResolvedDependencyPrinter printer) {
        printer.print(this);
    }

    static ResolvedDependency empty() {
        return EmptyResolvedDependency.INSTANCE;
    }
}
