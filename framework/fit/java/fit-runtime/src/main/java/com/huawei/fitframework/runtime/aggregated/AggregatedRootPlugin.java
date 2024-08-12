/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime.aggregated;

import com.huawei.fitframework.maven.MavenCoordinate;
import com.huawei.fitframework.model.Version;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginKey;
import com.huawei.fitframework.plugin.PluginMetadata;
import com.huawei.fitframework.plugin.support.DefaultPluginKey;
import com.huawei.fitframework.plugin.support.DefaultPluginMetadata;
import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.FitRuntimeStartupException;
import com.huawei.fitframework.runtime.support.AbstractRootPlugin;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 为根插件提供聚合启动场景的实现。
 *
 * @author 梁济时
 * @since 2023-01-31
 */
final class AggregatedRootPlugin extends AbstractRootPlugin {
    private static final String PLUGIN_ENTRY_PREFIX = "FIT-INF/plugins/";

    private final FitRuntime runtime;
    private final Jar jar;
    private final PluginMetadata metadata;

    /**
     * 使用所属的运行时初始化 {@link AggregatedRootPlugin} 类的新实例。
     *
     * @param runtime 表示所属运行时的 {@link FitRuntime}。
     */
    AggregatedRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;

        try {
            this.jar = Jar.from(this.runtime.location());
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format("Failed to load JAR of root plugin. [location={0}]",
                    this.runtime.location().toExternalForm()), e);
        }
        MavenCoordinate coordinate;
        try {
            coordinate = MavenCoordinate.read(this.jar);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format(
                    "Failed to read maven coordinate from JAR. [jar={0}]",
                    runtime.location().toExternalForm()), e);
        }
        PluginKey pluginKey = new DefaultPluginKey(coordinate.groupId(),
                coordinate.artifactId(),
                Version.parse(coordinate.version()));
        this.metadata =
                new DefaultPluginMetadata(pluginKey, runtime.location(), PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return AggregatedRootPlugin.class.getClassLoader();
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        List<Jar.Entry> pluginEntries = this.jar.entries()
                .stream()
                .filter(entry -> StringUtils.startsWithIgnoreCase(entry.name(), PLUGIN_ENTRY_PREFIX))
                .filter(entry -> StringUtils.endsWithIgnoreCase(entry.name(), Jar.FILE_EXTENSION))
                .collect(Collectors.toList());
        for (Jar.Entry pluginEntry : pluginEntries) {
            URL pluginUrl;
            try {
                pluginUrl = pluginEntry.location().asJar().toUrl();
            } catch (MalformedURLException e) {
                String message =
                        StringUtils.format("Failed to obtain URL of plugin. [location={0}]", pluginEntry.location());
                throw new FitRuntimeStartupException(message, e);
            }
            this.loadPlugin(pluginUrl);
        }
    }
}
