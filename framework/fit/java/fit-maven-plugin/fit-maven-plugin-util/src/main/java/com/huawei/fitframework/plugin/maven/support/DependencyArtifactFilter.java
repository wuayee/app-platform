/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven.support;

import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import java.util.Arrays;
import java.util.List;

/**
 * 为依赖提供归档件的过滤程序。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2023-02-03
 */
final class DependencyArtifactFilter implements ArtifactFilter {
    static final DependencyArtifactFilter INSTANCE = new DependencyArtifactFilter();

    private static final List<String> SCOPE_BLACK_LIST = Arrays.asList(Artifact.SCOPE_TEST, Artifact.SCOPE_PROVIDED);

    private DependencyArtifactFilter() {}

    @Override
    public boolean include(Artifact artifact) {
        return !SCOPE_BLACK_LIST.contains(artifact.getScope())
                && StringUtils.endsWithIgnoreCase(artifact.getFile().getName(), Jar.FILE_EXTENSION);
    }
}
