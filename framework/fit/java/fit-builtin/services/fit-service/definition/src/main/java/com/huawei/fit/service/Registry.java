/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.UniqueFitableId;

import java.util.List;

/**
 * 表示注册中心。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-17
 */
public interface Registry {
    /**
     * 订阅指定服务列表的服务。
     *
     * @param ids 表示指定服务唯一标识列表的 {@link List}{@code <}{@link UniqueFitableId}{@code >}。
     * @return 如果订阅成功，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean subscribeFitables(List<UniqueFitableId> ids);

    /**
     * 获取指定服务实现的地址列表。
     *
     * @param id 表示指定服务实现唯一标识的 {@link UniqueFitableId}。
     * @return 表示指定服务实现的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    List<Target> getFitableTargets(UniqueFitableId id);
}
