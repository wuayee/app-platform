/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.conf;

import com.huawei.fitframework.conf.support.AbstractConfigLoader;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 为 {@link ConfigLoader} 提供基于YAML格式的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-25
 */
public class YamlConfigLoader extends AbstractConfigLoader {
    private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(".yaml", ".yml"));

    @Override
    public Set<String> extensions() {
        return Collections.unmodifiableSet(EXTENSIONS);
    }

    @Override
    public ConfigLoadingResult load(Resource resource, String name) {
        String extension = FileUtils.extension(resource.filename());
        if (!EXTENSIONS.contains(StringUtils.toLowerCase(extension))) {
            return ConfigLoadingResult.failure();
        }
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, Object> configValues;
        try (InputStream in = resource.read()) {
            configValues = yaml.load(in);
        } catch (IOException | YAMLException e) {
            throw new ConfigLoadException(StringUtils.format("Failed to parse YAML from config resource. [url={0}]",
                    resource), e);
        } catch (ClassCastException e) {
            throw new ConfigLoadException(StringUtils.format("The content of config must be an object. [url={0}]",
                    resource), e);
        }
        String configName = this.nameOfConfig(resource, name);
        Config config = Config.fromHierarchical(configName, configValues);
        return ConfigLoadingResult.success(config);
    }
}
