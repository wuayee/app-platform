/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

import java.util.List;

/**
 * 表示动态路由器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-26
 */
public interface DynamicRouter {
    /**
     * 对指定的泛服务进行动态路由。
     *
     * @param genericable 表示指定泛服务的 {@link Genericable}。
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示动态路由后的泛服务实现列表的 {@link List}{@code <}{@link Fitable}{@code >}。
     */
    List<Fitable> route(Genericable genericable, InvocationContext context, Object[] args);
}
