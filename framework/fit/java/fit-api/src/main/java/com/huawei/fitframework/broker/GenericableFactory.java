/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

/**
 * 表示 {@link Genericable} 的工厂。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-26
 */
public interface GenericableFactory {
    /**
     * 根据指定的服务唯一标识和版本号创建一个可配置的服务。
     *
     * @param id 表示指定的服务的唯一标识的 {@link String}。
     * @param version 表示指定的服务的版本号的 {@link String}。
     * @return 表示创建的可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable create(String id, String version);

    /**
     * 根据指定的服务的唯一标识创建一个可配置的服务实现。
     *
     * @param id 表示指定的服务的唯一标识的 {@link UniqueGenericableId}。
     * @return 表示创建的可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable create(UniqueGenericableId id);
}
