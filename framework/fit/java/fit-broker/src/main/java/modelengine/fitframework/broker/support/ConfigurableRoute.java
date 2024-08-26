/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.Route;

/**
 * 表示可修改的 {@link Route}。
 *
 * @author 季聿阶
 * @since 2023-03-30
 */
public class ConfigurableRoute implements Route {
    private String defaultFitableId;

    @Override
    public String defaultFitable() {
        return this.defaultFitableId;
    }

    /**
     * 设置默认路由的服务实现唯一标识。
     *
     * @param defaultFitableId 表示待设置默认路由的服务实现唯一标识的 {@link String}。
     */
    public void defaultFitable(String defaultFitableId) {
        this.defaultFitableId = defaultFitableId;
    }
}
