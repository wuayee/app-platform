/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime.discrete;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

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
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 为根插件提供离散启动的实现。
 *
 * @author 季聿阶
 * @since 2023-07-29
 */
public class DiscreteRootPlugin extends AbstractRootPlugin {
    private final FitRuntime runtime;
    private final File frameworkDirectory;
    private final PluginMetadata metadata;

    DiscreteRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;
        this.frameworkDirectory = notNull(FileUtils.file(runtime.location()),
                () -> new FitRuntimeStartupException("The framework directory cannot be null."));
        isTrue(this.frameworkDirectory.isDirectory(),
                () -> new FitRuntimeStartupException("The framework directory is not a directory."));
        this.metadata = buildPluginMetadata(this.frameworkDirectory, runtime.location());
    }

    private static PluginMetadata buildPluginMetadata(File frameworkDirectory, URL location) {
        // 框架目录下存在 fit-discrete-launcher-*.jar 的启动包
        File[] files = frameworkDirectory.listFiles((file, name) -> isLauncher(name));
        if (ArrayUtils.isEmpty(files)) {
            throw new FitRuntimeStartupException("Failed to locate launcher JAR.");
        }
        File launcher = files[0];

        Jar jar;
        try {
            jar = Jar.from(launcher);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format("Failed to load JAR of launcher. [location={0}]",
                    FileUtils.path(launcher)), e);
        }
        MavenCoordinate coordinate;
        try {
            coordinate = MavenCoordinate.read(jar);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format(
                    "Failed to read maven coordinate from JAR. [jar={0}]",
                    jar), e);
        }
        PluginKey pluginKey = new DefaultPluginKey(coordinate.groupId(),
                coordinate.artifactId(),
                Version.parse(coordinate.version()));
        return new DefaultPluginMetadata(pluginKey, location, PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    private static boolean isLauncher(String name) {
        return StringUtils.startsWithIgnoreCase(name, "fit-discrete-launcher") && StringUtils.endsWithIgnoreCase(name,
                Jar.FILE_EXTENSION);
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return DiscreteRootPlugin.class.getClassLoader();
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        File pluginDirectory = new File(this.frameworkDirectory, "plugins");
        isTrue(pluginDirectory.isDirectory(),
                () -> new FitRuntimeStartupException("The plugin directory is not a directory."));
        File[] pluginFiles =
                pluginDirectory.listFiles((file, name) -> StringUtils.endsWithIgnoreCase(name, Jar.FILE_EXTENSION));
        if (pluginFiles == null) {
            return;
        }
        for (File pluginFile : pluginFiles) {
            this.loadPlugin(FileUtils.urlOf(pluginFile));
        }
    }
}
