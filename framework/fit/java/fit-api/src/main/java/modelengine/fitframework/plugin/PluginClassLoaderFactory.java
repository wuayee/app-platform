/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.plugin;

/**
 * 为插件所使用的类加载程序提供工厂。
 *
 * @author 梁济时
 * @since 2022-06-29
 */
@FunctionalInterface
public interface PluginClassLoaderFactory {
    /**
     * 为指定的插件创建类加载程序。
     *
     * @param metadata 表示待创建类加载程序的插件的元数据的 {@link PluginMetadata}。
     * @return 表示插件的类加载程序的 {@link ClassLoader}。
     */
    ClassLoader create(PluginMetadata metadata);
}
