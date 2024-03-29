/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.support;

import com.huawei.fitframework.build.util.ArtifactDownloader;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 为用以构建的 MOJO 提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-28
 */
public abstract class AbstractPackageMojo extends AbstractMojo {
    @Parameter(defaultValue = "${localRepository}", readonly = true)
    private ArtifactRepository localRepository;
    @Component
    private ArtifactResolver artifactResolver;
    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    protected ArtifactDownloader createDownloader() {
        return ArtifactDownloader.custom()
                .artifactResolver(this.artifactResolver)
                .artifactHandlerManager(this.artifactHandlerManager)
                .localRepository(this.localRepository)
                .remoteRepositories(this.project().getRemoteArtifactRepositories())
                .build();
    }
}
