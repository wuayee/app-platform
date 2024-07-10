/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.actuator;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.actuator.entity.AddressVo;
import com.huawei.fit.actuator.entity.EndpointVo;
import com.huawei.fit.actuator.entity.FitableVo;
import com.huawei.fit.actuator.entity.FormatVo;
import com.huawei.fit.actuator.entity.PluginVo;
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
import com.huawei.fitframework.conf.ConfigValueSupplier;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginComparators;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
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
     * @return 表示当前进程的所有插件列表的 {@link List}{@code <}{@link PluginVo}{@code >}。
     */
    @GetMapping(path = "/plugins")
    public List<PluginVo> getPlugins() {
        return this.fitRuntime.plugins()
                .stream()
                .sorted(PluginComparators.STARTUP)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定插件的指定配置键的值。
     * <p>当指定键为空白字符串时，返回所有的键。</p>
     *
     * @param pluginName 表示指定插件名的 {@link String}，当插件名为空白字符串时，则返回整个运行时的配置值。
     * @param key 表示指定配置键的 {@link String}。
     * @return 表示指定配置键的值的 {@link Object}。
     */
    @GetMapping(path = "/configs")
    public Object getConfigs(@RequestQuery(name = "plugin", required = false) String pluginName,
            @RequestQuery(name = "key", required = false) String key) {
        Config config;
        if (StringUtils.isBlank(pluginName)) {
            config = this.fitRuntime.config();
        } else {
            config = this.fitRuntime.plugin(pluginName).orElse(this.fitRuntime.root()).config();
        }
        if (StringUtils.isBlank(key)) {
            return config.keys();
        }
        if (config instanceof ConfigValueSupplier) {
            return ((ConfigValueSupplier) config).get(key);
        }
        return config.get(key, Object.class);
    }

    /**
     * 获取指定服务的所有服务实现列表。
     *
     * @param genericableId 表示指定服务的唯一标识的 {@link String}。
     * @return 表示指定服务的所有服务实现列表的 {@link List}{@code <}{@link FitableVo}{@code >}。
     */
    @GetMapping(path = "/fitables")
    public List<FitableVo> getFitables(@RequestQuery(name = "genericableId") String genericableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        Genericable genericable = this.brokerClient.getGenericable(genericableId);
        return genericable.fitables().stream().map(this::convert).collect(Collectors.toList());
    }

    /**
     * 获取指定服务实现的地址列表。
     *
     * @param genericableId 表示指定服务的唯一标识的 {@link String}。
     * @param fitableId 表示指定服务实现的唯一标识的 {@link String}。
     * @return 表示指定服务实现的地址列表的 {@link List}{@code <}{@link AddressVo}{@code >}。
     */
    @GetMapping(path = "/addresses")
    public List<AddressVo> getAddresses(@RequestQuery(name = "genericableId") String genericableId,
            @RequestQuery(name = "fitableId") String fitableId) {
        notBlank(genericableId, "The genericable id cannot be blank.");
        notBlank(fitableId, "The fitable id cannot be blank.");
        Genericable genericable =
                this.brokerClient.getRouter(genericableId).route(new FitableIdFilter(fitableId)).getGenericable();
        return genericable.fitable(fitableId, Fitable.DEFAULT_VERSION)
                .targets()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private PluginVo convert(Plugin plugin) {
        PluginVo vo = new PluginVo();
        vo.setGroup(plugin.metadata().group());
        vo.setName(plugin.metadata().name());
        vo.setVersion(plugin.metadata().version());
        vo.setCategory(plugin.metadata().category().getCode());
        vo.setLevel(plugin.metadata().level());
        return vo;
    }

    private FitableVo convert(Fitable fitable) {
        FitableVo vo = new FitableVo();
        vo.setId(fitable.id());
        vo.setVersion(fitable.version());
        vo.setAliases(fitable.aliases().all());
        vo.setTags(fitable.tags().all());
        vo.setDegradation(fitable.degradationFitableId());
        return vo;
    }

    private AddressVo convert(Target target) {
        AddressVo vo = new AddressVo();
        vo.setWorkerId(target.workerId());
        vo.setHost(target.host());
        vo.setEnvironment(target.environment());
        vo.setFormats(target.formats().stream().map(this::convert).collect(Collectors.toList()));
        vo.setEndpoints(target.endpoints().stream().map(this::convert).collect(Collectors.toList()));
        vo.setExtensions(target.extensions());
        return vo;
    }

    private FormatVo convert(Format format) {
        FormatVo vo = new FormatVo();
        vo.setName(format.name());
        vo.setCode(format.code());
        return vo;
    }

    private EndpointVo convert(Endpoint endpoint) {
        EndpointVo vo = new EndpointVo();
        vo.setProtocol(endpoint.protocol());
        vo.setCode(endpoint.protocolCode());
        vo.setPort(endpoint.port());
        return vo;
    }
}
