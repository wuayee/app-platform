/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime.direct;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.model.Version;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginKey;
import com.huawei.fitframework.plugin.PluginMetadata;
import com.huawei.fitframework.plugin.support.DefaultPluginKey;
import com.huawei.fitframework.plugin.support.DefaultPluginMetadata;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.support.AbstractRootPlugin;

import java.net.URL;

/**
 * 为根插件提供直接调用启动的实现。
 *
 * @author 梁济时
 * @since 2023-02-07
 */
final class DirectRootPlugin extends AbstractRootPlugin {
    private static final String APP_GROUP_CONFIG_KEY = "application.group";
    private static final String APP_NAME_CONFIG_KEY = "application.name";
    private static final String APP_VERSION_CONFIG_KEY = "application.version";

    private static final String UNKNOWN_APP_PROPERTY = "<unknown>";

    private final FitRuntime runtime;
    private final PluginMetadata metadata;

    DirectRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;
        this.metadata = buildPluginMetadata(runtime.config(), runtime.location());
    }

    private static PluginMetadata buildPluginMetadata(Config config, URL location) {
        String group = config.get(APP_GROUP_CONFIG_KEY, String.class);
        String name = config.get(APP_NAME_CONFIG_KEY, String.class);
        String version = config.get(APP_VERSION_CONFIG_KEY, String.class);
        // fit-runtime 包在 lib 目录中，lib 目录的上级目录为应用程序根目录
        PluginKey pluginKey = new DefaultPluginKey(nullIf(group, "direct.group"),
                nullIf(name, "direct"),
                Version.parse(nullIf(version, "0.0.1-direct")));
        return new DefaultPluginMetadata(pluginKey, location, PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    @Nonnull
    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Nonnull
    @Override
    public ClassLoader pluginClassLoader() {
        return DirectRootPlugin.class.getClassLoader();
    }

    @Nonnull
    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        // 插件都被加载到一起，没有单独的加载插件逻辑
    }

    @Override
    public Plugin loadPlugin(URL plugin) {
        return this;
    }

    @Override
    public Plugin unloadPlugin(URL plugin) {
        return this;
    }
}
