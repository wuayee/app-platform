/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven.support;

import static com.huawei.fitframework.plugin.maven.JarMavenCoordinateResolver.MAVEN_ENTRY_PREFIX;

import com.huawei.fitframework.plugin.maven.JarEntryResolver;
import com.huawei.fitframework.plugin.maven.MavenCoordinate;
import com.huawei.fitframework.plugin.maven.exception.FitMavenPluginException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;

/**
 * 对 pom.properties 文件进行解析的解析器。
 *
 * @author 梁济时 l00298979
 * @since 2020-11-26
 */
public class PomPropertiesEntryResolver implements JarEntryResolver<MavenCoordinate> {
    /** 表示 {@link PomPropertiesEntryResolver} 解析器的单例。 */
    public static final PomPropertiesEntryResolver INSTANCE = new PomPropertiesEntryResolver();

    @Override
    public boolean is(JarEntry entry) {
        String name = entry.getName();
        return name.startsWith(MAVEN_ENTRY_PREFIX) && name.endsWith("pom.properties");
    }

    @Override
    public MavenCoordinate resolve(InputStream in) {
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException ex) {
            throw new FitMavenPluginException("Fail to load pom.properties as a properties file.", ex);
        }
        return MavenCoordinate.builder()
                .setGroupId(properties.getProperty("groupId"))
                .setArtifactId(properties.getProperty("artifactId"))
                .setVersion(properties.getProperty("version"))
                .build();
    }
}
