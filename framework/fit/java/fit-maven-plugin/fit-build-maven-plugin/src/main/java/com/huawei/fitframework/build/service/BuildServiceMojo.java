/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.service;

import com.huawei.fitframework.plugin.maven.support.AbstractMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.Objects;

/**
 * 表示编译服务的任务。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-17
 */
@Mojo(name = "build-service", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuildServiceMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.packaging}", readonly = true, required = true)
    private String packaging;

    @Override
    public void execute() throws MojoExecutionException {
        if (!Objects.equals(this.packaging, "jar")) {
            return;
        }
        ServiceCompiler compiler = new ServiceCompiler(this.project(), this.getLog());
        compiler.compile();
    }
}
