/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.conf.runtime.MatataConfig.Registry;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Registry} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-06-27
 */
public class DefaultRegistry implements Registry {
    private String host;
    private int port;
    private int protocol;
    private String environment;
    private List<DefaultAvailableService> availableServices;
    private Map<String, Object> extensions;

    /**
     * 设置主机地址的配置。
     *
     * @param host 表示待设置的主机地址配置的 {@link String}。
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 设置端口号的配置。
     *
     * @param port 表示待设置的端口号配置的 {@code int}。
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 设置传输协议的配置。
     *
     * @param protocol 表示待设置的传输协议配置的 {@code int}。
     */
    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    /**
     * 设置环境的配置。
     *
     * @param environment 表示待设置的环境配置的 {@link String}。
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * 设置可用服务地址的配置列表。
     *
     * @param availableServices 表示待设置的可用服务地址的配置列表的 {@link List}{@code <}{@link
     * DefaultAvailableService}{@code >}。
     */
    public void setAvailableServices(List<DefaultAvailableService> availableServices) {
        this.availableServices = availableServices;
    }

    /**
     * 设置扩展信息集合。
     *
     * @param extensions 表示待设置的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public int protocolCode() {
        return this.protocol;
    }

    @Override
    public CommunicationProtocol protocol() {
        return CommunicationProtocol.from(this.protocol);
    }

    @Override
    public String environment() {
        return this.environment;
    }

    @Override
    public List<AvailableService> availableServices() {
        if (this.availableServices == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.availableServices);
    }

    @Override
    public Map<String, Object> extensions() {
        if (this.extensions == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.extensions);
    }

    @Override
    public Map<String, String> visualExtensions() {
        Map<String, String> visualExtensions = new HashMap<>();
        Map<String, String> flattenedMap = MapUtils.flat(this.extensions(), ".");
        for (Map.Entry<String, String> entry : flattenedMap.entrySet()) {
            visualExtensions.put(Config.visualizeKey(entry.getKey()), entry.getValue());
        }
        return visualExtensions;
    }

    @Override
    public String toString() {
        return StringUtils.format("/{\"host\": \"{0}\", \"port\": {1}, \"protocol\": {2}, \"environment\": \"{3}\", "
                        + "\"available-services\": {4}, \"extensions\": {5}/}",
                this.host,
                this.port,
                this.protocol,
                this.environment,
                this.availableServices,
                this.extensions);
    }
}
