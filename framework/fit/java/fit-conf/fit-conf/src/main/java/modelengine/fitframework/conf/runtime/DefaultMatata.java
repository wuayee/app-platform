/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.util.StringUtils;

/**
 * 表示 {@link MatataConfig} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-06-27
 */
public class DefaultMatata implements MatataConfig {
    private DefaultRegistry registry;

    /**
     * 设置注册中心的配置。
     *
     * @param registry 表示待设置的注册中心的配置的 {@link DefaultRegistry}。
     */
    public void setRegistry(DefaultRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Registry registry() {
        return this.registry;
    }

    @Override
    public String toString() {
        String content = StringUtils.format("/{\"registry\": {0}/}", this.registry);
        return StringUtils.format("/{\"matata\": {0}/}", content);
    }
}
