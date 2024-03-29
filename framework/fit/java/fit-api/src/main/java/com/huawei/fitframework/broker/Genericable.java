/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

import java.util.List;

/**
 * 表示可执行的泛服务对象。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-07
 */
public interface Genericable extends GenericableMetadata {
    /**
     * 获取服务的所有实现列表。
     *
     * @return 表示服务的所有实现列表的 {@link List}{@code <}{@link Fitable}{@code >}。
     */
    @Override
    List<Fitable> fitables();

    /**
     * 执行服务。
     *
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(InvocationContext context, Object[] args);
}
