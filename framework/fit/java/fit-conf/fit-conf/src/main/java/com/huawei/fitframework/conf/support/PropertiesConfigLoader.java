/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.conf.support;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.conf.ConfigLoadException;
import com.huawei.fitframework.conf.ConfigLoadingResult;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * 为 {@link Properties} 格式的配置提供加载程序。
 *
 * @author 梁济时
 * @since 2022-12-19
 */
public class PropertiesConfigLoader extends AbstractConfigLoader {
    private static final String FILE_EXTENSION = ".properties";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public Set<String> extensions() {
        return Collections.singleton(FILE_EXTENSION);
    }

    @Override
    public ConfigLoadingResult load(Resource resource, String name) {
        if (!StringUtils.equalsIgnoreCase(FileUtils.extension(resource.filename()), FILE_EXTENSION)) {
            return ConfigLoadingResult.failure();
        }
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(resource.read(), CHARSET)) {
            properties.load(reader);
        } catch (IOException ex) {
            throw new ConfigLoadException(StringUtils.format(
                    "Failed to load data of config from resource. [resource={0}]",
                    resource), ex);
        }
        String configName = this.nameOfConfig(resource, name);
        Config config = Config.fromProperties(configName, properties);
        return ConfigLoadingResult.success(config);
    }
}
