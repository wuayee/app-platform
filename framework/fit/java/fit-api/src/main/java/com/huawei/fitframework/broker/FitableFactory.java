/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

/**
 * 表示 {@link Fitable} 的工厂。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-24
 */
public interface FitableFactory {
    /**
     * 根据指定的服务实现唯一标识和版本号创建一个可配置的服务实现。
     *
     * @param id 表示指定的服务实现的唯一标识的 {@link String}。
     * @param version 表示指定的服务实现的版本号的 {@link String}。
     * @return 表示创建的可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable create(String id, String version);

    /**
     * 根据指定的服务实现的唯一标识创建一个可配置的服务实现。
     *
     * @param id 表示指定的服务实现的唯一标识的 {@link UniqueFitableId}。
     * @return 表示创建的可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable create(UniqueFitableId id);

    /**
     * 根据指定的服务实现创建一个可配置的服务实现。
     *
     * @param fitable 表示指定的服务实现的 {@link Fitable}。
     * @return 表示创建的可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable create(Fitable fitable);
}
