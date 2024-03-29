/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime;

import com.huawei.fitframework.broker.UniqueFitableId;

import java.util.List;
import java.util.Map;

/**
 * 表示运行时 {@code 'matata.'} 前缀的配置项。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-27
 */
public interface MatataConfig {
    /**
     * 获取 {@code 'matata.registry.'} 前缀的配置项。
     *
     * @return 表示 {@code 'matata.registry.'} 前缀的配置项的 {@link Registry}。
     */
    Registry registry();

    /**
     * 表示 {@code 'matata.registry.'} 前缀的配置项。
     */
    interface Registry {
        /**
         * 获取 {@code 'matata.registry.host'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.host'} 的配置项的 {@link String}。
         */
        String host();

        /**
         * 获取 {@code 'matata.registry.port'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.port'} 的配置项的 {@code int}。
         */
        int port();

        /**
         * 获取 {@code 'matata.registry.protocol'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.protocol'} 的配置项的 {@code int}。
         */
        int protocolCode();

        /**
         * 获取 {@code 'matata.registry.protocol'} 的配置项的含义。
         *
         * @return 表示 {@code 'matata.registry.protocol'} 的配置项的含义的 {@link CommunicationProtocol}。
         */
        CommunicationProtocol protocol();

        /**
         * 获取 {@code 'matata.registry.environment'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.environment'} 的配置项的 {@link String}。
         */
        String environment();

        /**
         * 获取 {@code 'matata.registry.available-services'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.available-services'} 的配置项的 {@link List}{@code
         * <}{@link AvailableService}{@code >}。
         */
        List<AvailableService> availableServices();

        /**
         * 获取 {@code 'matata.registry.extensions'} 的配置项。
         *
         * @return 表示 {@code 'matata.registry.extensions'} 的配置项的
         * {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        Map<String, Object> extensions();

        /**
         * 获取 {@code 'matata.registry.extensions'} 的配置项的可视化信息。
         *
         * @return 表示 {@code 'matata.registry.extensions'} 的配置项的可视化信息的
         * {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
         */
        Map<String, String> visualExtensions();

        /**
         * 表示 {@code 'matata.registry'} 中关于可用服务的配置项。
         */
        interface AvailableService {
            /**
             * 获取 {@code 'matata.registry.available-services[*].genericable-name'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].genericable-name'} 的配置项的 {@link String}。
             */
            String genericableName();

            /**
             * 获取 {@code 'matata.registry.available-services[*].genericable-id'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].genericable-id'} 的配置项的 {@link String}。
             */
            String genericableId();

            /**
             * 获取 {@code 'matata.registry.available-services[*].genericable-version'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].genericable-version'} 的配置项的 {@link String}。
             */
            String genericableVersion();

            /**
             * 获取 {@code 'matata.registry.available-services[*].fitable-id'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].fitable-id'} 的配置项的 {@link String}。
             */
            String fitableId();

            /**
             * 获取 {@code 'matata.registry.available-services[*].fitable-version'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].fitable-version'} 的配置项的 {@link String}。
             */
            String fitableVersion();

            /**
             * 获取 {@code 'matata.registry.available-services[*].formats'} 的配置项。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].formats'} 的配置项的 {@link List}{@code
             * <}{@link Integer}{@code >}。
             */
            List<Integer> formatCodes();

            /**
             * 获取 {@code 'matata.registry.available-services[*].formats'} 的配置项的含义。
             *
             * @return 表示 {@code 'matata.registry.available-services[*].formats'} 的配置项的含义的 {@link List}{@code
             * <}{@link SerializationFormat}{@code >}。
             */
            List<SerializationFormat> formats();

            /**
             * 将当前配置项转换为服务实现的唯一标识。
             *
             * @return 表示服务实现的唯一标识的 {@link UniqueFitableId}。
             */
            UniqueFitableId toUniqueId();
        }
    }
}
