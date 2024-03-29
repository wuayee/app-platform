/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.conf.support;

import com.huawei.fitframework.conf.ConfigLoader;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link ConfigLoader} 提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2022-12-19
 */
public abstract class AbstractConfigLoader implements ConfigLoader {
    /**
     * 获取配置的名称。
     * <p>若名称是空白字符串，则从 {@link Resource} 的信息来计算资源名称。</p>
     *
     * @param resource 表示配置的资源的 {@link Resource}。
     * @param name 表示配置的名称的 {@link String}。
     * @return 表示配置的名称的 {@link String}。
     */
    protected String nameOfConfig(Resource resource, String name) {
        String actual = StringUtils.trim(name);
        if (StringUtils.isEmpty(actual)) {
            actual = FileUtils.ignoreExtension(resource.filename());
        }
        return actual;
    }
}
