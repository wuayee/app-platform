/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.actuator;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.Endpoint;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.Format;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示监控的控制器。
 *
 * @author 季聿阶
 * @since 2024-07-04
 */
@DocumentIgnored
@Component
@RequestMapping(path = "/actuator")
public class ActuatorController {
    private final FitRuntime fitRuntime;
    private final BrokerClient brokerClient;

    ActuatorController(FitRuntime fitRuntime, BrokerClient brokerClient) {
        this.fitRuntime = notNull(fitRuntime, "The FIT runtime cannot be null.");
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
    }

    /**
     * 获取当前进程的所有的插件列表。
     *
     * @return 表示当前进程的所有插件列表的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @GetMapping(path = "/plugins")
    public Map<String, Object> getPlugins() {
        return this.fitRuntime.plugins()
                .stream()
                .collect(Collectors.toMap(plugin -> plugin.metadata().name(), this::toMap));
    }

    /**
     * 获取指定插件的指定配置键的值。
     *
     * @param pluginName 表示指定插件名的 {@link String}，当插件名为空白字符串时，则返回整个运行时的配置值。
     * @param key 表示指定配置键的 {@link String}。
     * @return 表示指定配置键的值的 {@link Object}。
     */
    @GetMapping(path = "/configs")
    public Object getConfigs(@RequestQuery(name = "plugin", required = false) String pluginName,
            @RequestQuery(name = "key") String key) {
        Config config;
        if (StringUtils.isBlank(pluginName)) {
            config = this.fitRuntime.config();
        } else {
            config = this.fitRuntime.plugin(pluginName).orElse(this.fitRuntime.root()).config();
        }
        return config.get(key, Object.class);
    }

    /**
     * 获取指定服务的所有服务实现列表。
     *
     * @param genericableId 表示指定服务的唯一标识的 {@link String}。
     * @return 表示指定服务的所有服务实现列表的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,
     * }{@link Object}{@code >>}。
     */
    @GetMapping(path = "/fitables")
    public List<Map<String, Object>> getFitables(@RequestQuery(name = "genericableId") String genericableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        Genericable genericable = this.brokerClient.getGenericable(genericableId);
        return genericable.fitables().stream().map(this::toMap).collect(Collectors.toList());
    }

    /**
     * 获取指定服务实现的地址列表。
     *
     * @param genericableId 表示指定服务的唯一标识的 {@link String}。
     * @param fitableId 表示指定服务实现的唯一标识的 {@link String}。
     * @return 表示指定服务实现的地址列表的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,
     * }{@link Object}{@code >>}。
     */
    @GetMapping(path = "/addresses")
    public List<Map<String, Object>> getAddresses(@RequestQuery(name = "genericableId") String genericableId,
            @RequestQuery(name = "fitableId") String fitableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        notBlank(fitableId, "The fitable id cannot be blank.");
        Genericable genericable =
                this.brokerClient.getRouter(genericableId).route(new FitableIdFilter(fitableId)).getGenericable();
        return genericable.fitable(fitableId, Fitable.DEFAULT_VERSION)
                .targets()
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(Plugin plugin) {
        return MapBuilder.<String, Object>get()
                .put("group", plugin.metadata().group())
                .put("name", plugin.metadata().name())
                .put("version", plugin.metadata().version())
                .put("category", plugin.metadata().category().getCode())
                .put("level", plugin.metadata().level())
                .build();
    }

    private Map<String, Object> toMap(Fitable fitable) {
        return MapBuilder.<String, Object>get()
                .put("id", fitable.id())
                .put("version", fitable.version())
                .put("aliases", fitable.aliases().all())
                .put("tags", fitable.tags().all())
                .put("degradation", fitable.degradationFitableId())
                .build();
    }

    private Map<String, Object> toMap(Target target) {
        return MapBuilder.<String, Object>get()
                .put("workerId", target.workerId())
                .put("host", target.host())
                .put("environment", target.environment())
                .put("formats", target.formats().stream().map(this::toMap).collect(Collectors.toList()))
                .put("endpoints", target.endpoints().stream().map(this::toMap).collect(Collectors.toList()))
                .put("extensions", target.extensions())
                .build();
    }

    private Map<String, Object> toMap(Format format) {
        return MapBuilder.<String, Object>get().put("name", format.name()).put("code", format.code()).build();
    }

    private Map<String, Object> toMap(Endpoint endpoint) {
        return MapBuilder.<String, Object>get()
                .put("protocol", endpoint.protocol())
                .put("code", endpoint.protocolCode())
                .put("port", endpoint.port())
                .build();
    }
}
