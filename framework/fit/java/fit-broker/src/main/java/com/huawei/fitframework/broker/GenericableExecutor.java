/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

import java.util.List;

/**
 * 表示泛服务的执行器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-27
 */
public interface GenericableExecutor {
    /**
     * 执行泛服务的指定实现列表。
     *
     * @param fitables 表示泛服务的指定实现列表的 {@link List}{@code <}{@link Fitable}{@code >}。
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(List<Fitable> fitables, InvocationContext context, Object[] args);
}
