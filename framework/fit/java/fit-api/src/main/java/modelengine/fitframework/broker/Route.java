/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.broker;

/**
 * 表示泛服务的路由信息。
 *
 * @author 季聿阶
 * @since 2023-03-08
 */
public interface Route {
    /**
     * 获取默认路由的服务实现唯一标识。
     *
     * @return 表示默认路由的服务实现唯一标识的 {@link String}。
     */
    String defaultFitable();
}
