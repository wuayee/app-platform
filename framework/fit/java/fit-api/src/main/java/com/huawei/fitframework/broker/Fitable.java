/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

/**
 * 表示可执行的泛服务实现对象。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-07
 */
public interface Fitable extends FitableMetadata {
    /**
     * 获取服务实现所对应的服务。
     *
     * @return 表示服务实现所对应的服务的 {@link Genericable}。
     */
    @Override
    Genericable genericable();

    /**
     * 执行服务实现。
     *
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(InvocationContext context, Object[] args);
}
