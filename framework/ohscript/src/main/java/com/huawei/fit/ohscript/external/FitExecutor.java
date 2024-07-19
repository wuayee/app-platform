/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.external;

import com.huawei.fit.ohscript.external.support.DefaultFitExecutor;
import com.huawei.fit.ohscript.script.interpreter.ReturnValue;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.ioc.BeanContainer;

/**
 * 表示 FIT 调用的执行器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-18
 */
@FunctionalInterface
public interface FitExecutor {
    /**
     * 创建一个新的 FIT 调用的执行器。
     *
     * @param container 表示 bean 容器的 {@link BeanContainer}。
     * @param brokerClient 表示 FIT 调用代理客户端的 {@link BrokerClient}。
     * @return 表示创建出来的 FIT 调用的执行器的 {@link FitExecutor}。
     */
    static FitExecutor create(BeanContainer container, BrokerClient brokerClient) {
        return new DefaultFitExecutor(container, brokerClient);
    }

    /**
     * 使用 FIT 客户端代理，调用指定泛服务。
     *
     * @param genericableId 表示目标泛服务的唯一标识的 {@link String}。
     * @param args 表示 FIT 调用参数的 {@link Object}{@code []}。
     * <p>其中，参数长度最小为 1，第一个参数类型为 {@link java.util.Map}{@code <}{@link String}{@code ,
     * }{@link ReturnValue}{@code >}。后续参数，为指定泛服务的真实参数。</p>
     * @return 表示 FIT 响应结果的 {@link Object}。
     */
    Object execute(String genericableId, Object[] args);
}
