/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.oms.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 表示 Nacos 服务客户端。
 *
 * @author 何天放
 * @since 2024-11-20
 */
public interface NacosClient {
    /**
     * 注册服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @param ip 表示服务所在主机的 {@link String}。
     * @param port 表示服务所在端口的 {@code int}。
     */
    void registerService(String serviceName, String ip, int port);

    /**
     * 取消注册服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @param ip 表示服务所在主机的 {@link String}。
     * @param port 表示服务所在端口的 {@code int}。
     */
    void unregisterService(String serviceName, String ip, int port);

    /**
     * 查询服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @return 返回服务实例的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    List<Instance> queryService(String serviceName);
}
