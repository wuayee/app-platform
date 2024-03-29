/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.client;

import java.lang.reflect.Method;

/**
 * 为路由器提供工厂方法。
 *
 * @author 季聿阶 j00559309
 * @since 2021-10-26
 */
public interface RouterFactory {
    /**
     * 创建一个调用的动态路由器。
     *
     * @param genericableId 表示待调用的泛服务的唯一标识的 {@link String}。
     * @param isMicro 表示待调用的泛服务是否为微观服务的标记的 {@code boolean}。
     * @param genericableMethod 表示待调用的泛服务的方法的 {@link Method}。
     * @return 表示调用的动态路由器的 {@link Router}。
     */
    Router create(String genericableId, boolean isMicro, Method genericableMethod);
}
