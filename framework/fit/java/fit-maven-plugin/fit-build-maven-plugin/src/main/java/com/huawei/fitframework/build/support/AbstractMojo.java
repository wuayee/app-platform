/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.support;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 表示 Mojo 抽象父类。
 *
 * @author 季聿阶 j00559309
 * @since 2023-07-23
 */
public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private volatile boolean dependencyFilterAccepted = false;
    private final Object monitor = new byte[0];

    /**
     * 获取 Maven 项目。
     *
     * @return 表示当前 Maven 项目的 {@link MavenProject}。
     */
    protected final MavenProject project() {
        if (!this.dependencyFilterAccepted) {
            synchronized (this.monitor) {
                if (!this.dependencyFilterAccepted) {
                    this.project.setArtifactFilter(DependencyArtifactFilter.INSTANCE);
                    this.dependencyFilterAccepted = true;
                }
            }
        }
        return this.project;
    }
}
