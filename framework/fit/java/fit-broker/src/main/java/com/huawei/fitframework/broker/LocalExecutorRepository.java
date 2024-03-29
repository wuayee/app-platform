/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.UniqueGenericableId;

import java.util.Optional;
import java.util.Set;

/**
 * 为服务实现的本地执行器提供仓库。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-09-24
 */
public interface LocalExecutorRepository {
    /**
     * 获取仓库的注册入口。
     *
     * @return 表示注册入口的 {@link Registry}。
     */
    Registry registry();

    /**
     * 获取仓库的名称。
     *
     * @return 表示仓库名称的 {@link String}。
     */
    String name();

    /**
     * 获取仓库内的所有服务实现的本地执行器集合。
     *
     * @return 表示仓库内的所有服务实现的本地执行器集合的 {@link Set}{@code <}{@link LocalExecutor}{@code >}。
     */
    Set<LocalExecutor> executors();

    /**
     * 获取仓库内的指定服务的服务实现的本地执行器集合。
     *
     * @param id 表示指定服务的唯一标识的 {@link UniqueGenericableId}。
     * @return 仓库内的指定服务的服务实现的本地执行器集合的 {@link Set}{@code <}{@link LocalExecutor}{@code >}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 时。
     */
    Set<LocalExecutor> executors(UniqueGenericableId id);

    /**
     * 获取仓库内的指定服务实现的本地执行器。
     *
     * @param id 表示指定服务实现的唯一标识的 {@link UniqueFitableId}。
     * @return 表示仓库内的指定服务实现的本地执行器的 {@link Optional}{@code <}{@link LocalExecutor}{@code >}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 时。
     */
    Optional<LocalExecutor> executor(UniqueFitableId id);

    /**
     * 为本地执行器仓库提供注册入口。
     *
     * @author 梁济时 l00815032
     * @author 季聿阶 j00559309
     * @since 2020-09-24
     */
    @FunctionalInterface
    interface Registry {
        /**
         * 注册一个本地执行器的实例。
         * <p>当 {@code executor} 为 {@code null} 时，将移除对指定服务实现的本地执行器。</p>
         *
         * @param uniqueFitableId 表示指定服务实现的唯一标识的 {@link UniqueFitableId}。
         * @param executor 表示本地执行器的 {@link LocalExecutor}。若为 {@code null} 则移除本地实现。
         * @throws IllegalArgumentException 当 {@code uniqueFitableId} 为 {@code null} 时。
         */
        void register(UniqueFitableId uniqueFitableId, LocalExecutor executor);
    }
}
