/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven;

import com.huawei.fitframework.plugin.maven.support.DefaultDependencyResolver;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.function.Predicate;

/**
 * 解析文件所依赖Maven项的接口类
 *
 * @author l00298979
 * @since 2020-10-10
 */
public interface DependencyResolver {
    ResolvedDependency resolve() throws MojoExecutionException;

    interface Builder {
        Builder setMavenProject(MavenProject project);

        Builder setSourceCodeDirectory(String sourceCodeDirectory);

        Builder filterDependency(Predicate<Dependency> dependencyPredicate);

        ResolvedDependency resolve() throws MojoExecutionException;
    }

    static Builder custom() {
        return new DefaultDependencyResolver.Builder();
    }
}
