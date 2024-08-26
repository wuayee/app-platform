/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.plugin;

import modelengine.fitframework.plugin.maven.support.AbstractMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.Objects;

/**
 * 表示编译工具插件的任务。
 *
 * @author 杭潇
 * @since 2024-06-13
 */
@Mojo(name = "build-tool", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuildToolPluginMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.packaging}", readonly = true, required = true)
    private String packaging;

    @Override
    public void execute() throws MojoExecutionException {
        if (!Objects.equals(this.packaging, "jar")) {
            return;
        }
        ToolPluginCompiler compiler = new ToolPluginCompiler(this.project(), this.getLog());
        compiler.compile();
    }
}

