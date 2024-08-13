/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.service.util;

import com.huawei.fit.service.entity.Address;
import com.huawei.fit.service.entity.ApplicationInstance;
import com.huawei.fit.service.entity.FitableAddressInstance;
import com.huawei.fit.service.entity.Worker;
import com.huawei.fitframework.broker.Endpoint;
import com.huawei.fitframework.broker.Format;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.conf.runtime.CommunicationProtocol;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 处理 {@link FitableAddressInstance} 的工具类。
 *
 * @author 季聿阶
 * @since 2022-09-17
 */
public class FitableInstanceUtils {
    /**
     * 将指定的服务实例转化成地址列表。
     *
     * @param fitableInstance 表示指定的服务实例的 {@link FitableAddressInstance}。
     * @return 表示转化后的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    public static List<Target> toTargets(FitableAddressInstance fitableInstance) {
        return Optional.of(fitableInstance)
                .map(FitableAddressInstance::getApplicationInstances)
                .filter(CollectionUtils::isNotEmpty)
                .map(applicationInstances -> applicationInstances.stream()
                        .filter(Objects::nonNull)
                        .map(FitableInstanceUtils::toTargets)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static List<Target> toTargets(ApplicationInstance applicationInstance) {
        return Optional.of(applicationInstance)
                .map(ApplicationInstance::getWorkers)
                .filter(CollectionUtils::isNotEmpty)
                .map(workers -> workers.stream()
                        .filter(Objects::nonNull)
                        .map(worker -> toTargets(worker, applicationInstance.getFormats()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static List<Target> toTargets(Worker worker, List<Integer> formats) {
        List<Address> actualAddresses =
                worker.getAddresses().stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(actualAddresses)) {
            Address address = new Address();
            address.setEndpoints(new ArrayList<>());
            actualAddresses.add(address);
        }
        return actualAddresses.stream()
                .map(address -> toTarget(worker, formats, address))
                .distinct()
                .collect(Collectors.toList());
    }

    private static Target toTarget(Worker worker, List<Integer> formatCodes, Address address) {
        List<Format> formats = formatCodes.stream()
                .map(code -> Format.custom()
                        .name(SerializationFormat.from(code).name())
                        .code(code)
                        .build())
                .collect(Collectors.toList());
        return Target.custom()
                .formats(formats)
                .workerId(worker.getId())
                .host(address.getHost())
                .environment(worker.getEnvironment())
                .endpoints(toTargetEndPoints(address.getEndpoints()))
                .extensions(worker.getExtensions())
                .build();
    }

    private static List<Endpoint> toTargetEndPoints(List<com.huawei.fit.service.entity.Endpoint> endpoints) {
        if (CollectionUtils.isEmpty(endpoints)) {
            return Collections.emptyList();
        }
        return endpoints.stream()
                .filter(Objects::nonNull)
                .filter(endpoint -> endpoint.getPort() != null)
                .filter(endpoint -> endpoint.getProtocol() != null)
                .map(endpoint -> Endpoint.custom()
                        .port(endpoint.getPort())
                        .protocol(CommunicationProtocol.from(endpoint.getProtocol()).name(), endpoint.getProtocol())
                        .build())
                .collect(Collectors.toList());
    }
}
