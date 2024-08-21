/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.plugin.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.jvm.scan.support.ClassLoaderPackageScanner;
import modelengine.fitframework.util.ObjectUtils;

import java.net.URL;
import java.util.Enumeration;

/**
 * 表示 {@link ClassLoaderPackageScanner} 的插件类加载的实现。
 *
 * @author 季聿阶
 * @since 2023-09-01
 */
public class PluginClassLoaderScanner extends ClassLoaderPackageScanner {
    public PluginClassLoaderScanner(ClassLoader loader, PackageScanner.Callback callback) {
        super(loader, callback);
    }

    @Override
    protected Enumeration<URL> getPackageResources(String basePackage, String resourceName) {
        if (this.getLoader() instanceof PluginClassLoader) {
            PluginClassLoader pluginClassLoader = ObjectUtils.cast(this.getLoader());
            return pluginClassLoader.findResources(resourceName);
        } else {
            return super.getPackageResources(basePackage, resourceName);
        }
    }
}
