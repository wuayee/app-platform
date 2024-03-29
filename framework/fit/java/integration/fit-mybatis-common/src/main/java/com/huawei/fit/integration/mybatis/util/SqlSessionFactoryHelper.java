/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.integration.mybatis.util;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.jvm.scan.PackageScanner;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginKey;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.StringUtils;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 Mybatis 提供统一的工具类。
 *
 * @author 季聿阶 j00559309
 * @since 2023-04-05
 */
public final class SqlSessionFactoryHelper {
    private static final String CONFIG_PREFIX = "mybatis.";

    private SqlSessionFactoryHelper() {}

    /**
     * 提取配置信息中的 Mybatis 的配置，转换成 {@link Properties}。
     *
     * @param config 表示配置信息的 {@link Config}。
     * @return 表示转换后的信息的 {@link Properties}。
     */
    public static Properties properties(Config config) {
        Properties properties = new Properties();
        Set<String> keys =
                config.keys().stream().filter(key -> key.startsWith(CONFIG_PREFIX)).collect(Collectors.toSet());
        for (String key : keys) {
            String actualKey = key.substring(CONFIG_PREFIX.length());
            String value = config.get(key, String.class);
            properties.setProperty(actualKey, value);
        }
        return properties;
    }

    /**
     * 加载配置信息中指定位置的映射器集合。
     *
     * @param properties 表示配置信息的 {@link Properties}。
     * @param plugin 表示当前插件的 {@link Plugin}。
     * @param configuration 表示 Mybatis 的配置类的 {@link Configuration}。
     */
    public static void loadMappers(Properties properties, Plugin plugin, Configuration configuration) {
        loadMappersFromXml(properties, plugin, configuration);
        scanMappersInPackage(properties, plugin, configuration);
    }

    private static void loadMappersFromXml(Properties properties, Plugin plugin, Configuration configuration) {
        String mapperLocations = properties.getProperty(Config.canonicalizeKey("mapper-locations"));
        if (mapperLocations != null) {
            try {
                Resource[] resources = plugin.resolverOfResources().resolve(mapperLocations);
                loadMappersFromXml(configuration, resources);
            } catch (IOException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to resolve mapper resources in class loader. [plugin={0}, pattern={1}, error={2}]",
                        PluginKey.identify(plugin.metadata()),
                        mapperLocations,
                        ex.getMessage()), ex);
            }
        }
    }

    private static void loadMappersFromXml(Configuration configuration, Resource[] resources) throws IOException {
        for (Resource resource : resources) {
            try (InputStream in = resource.read()) {
                XMLMapperBuilder builder =
                        new XMLMapperBuilder(in, configuration, resource.filename(), configuration.getSqlFragments());
                builder.parse();
            }
        }
    }

    private static void scanMappersInPackage(Properties properties, Plugin plugin, Configuration configuration) {
        String mapperScan = properties.getProperty(Config.canonicalizeKey("mapper-scan"));
        if (mapperScan != null) {
            PackageScanner.forClassLoader(plugin.pluginClassLoader(), ((scanner, clazz) -> {
                MapperAnnotationBuilder builder = new MapperAnnotationBuilder(configuration, clazz);
                builder.parse();
                configuration.addMapper(clazz);
            })).scan(Collections.singletonList(mapperScan));
        }
    }
}
