/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.loadbalance;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.client.Client;
import com.huawei.fitframework.broker.Endpoint;
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Format;
import com.huawei.fitframework.broker.SerializationService;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.conf.runtime.CommunicationProtocol;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通信协议及序列化方式支持的负载均衡策略。
 *
 * @author 季聿阶 j00559309
 * @since 2022-06-06
 */
public class ProtocolAndFormatSupportedFilter extends AbstractFilter {
    private static final Logger log = Logger.get(ProtocolAndFormatSupportedFilter.class);

    private final List<Client> clients;
    private final SerializationService serializationService;
    private final CommunicationProtocol specifiedProtocol;
    private final SerializationFormat specifiedFormat;

    public ProtocolAndFormatSupportedFilter(List<Client> clients, SerializationService serializationService,
            CommunicationProtocol specifiedProtocol, SerializationFormat specifiedFormat) {
        this.clients = notNull(clients, "The clients cannot be null.");
        this.serializationService = notNull(serializationService, "The serialization service cannot be null.");
        this.specifiedProtocol = nullIf(specifiedProtocol, CommunicationProtocol.UNKNOWN);
        this.specifiedFormat = nullIf(specifiedFormat, SerializationFormat.UNKNOWN);
    }

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        return toFilterTargets.stream()
                .map(target -> this.getSupportedTarget(fitable, target, localWorkerId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Target getSupportedTarget(FitableMetadata fitable, Target target, String localWorkerId) {
        if (this.isLocal(target, localWorkerId)) {
            return target;
        }
        List<Endpoint> endpoints = target.endpoints()
                .stream()
                .filter(endpoint -> this.isProtocolSupported(endpoint.protocol()))
                .filter(endpoint -> this.isProtocolSpecified(endpoint.protocol()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(endpoints)) {
            log.debug("Target is filtered: no available protocol. [fitable={}, target={}]",
                    fitable.toUniqueId(),
                    target);
            return null;
        }
        List<Integer> supportedFormatCodes =
                this.serializationService.getSupportedFormats(fitable.genericable().method().method());
        Set<Integer> targetFormatCodes =
                target.formats().stream().map(Format::code).filter(this::isFormatSpecified).collect(Collectors.toSet());
        Set<Integer> availableFormatCodes = CollectionUtils.intersectOrdered(supportedFormatCodes, targetFormatCodes);
        if (availableFormatCodes.isEmpty()) {
            log.debug("Target is filtered: no available formats. [fitable={}, target={}]",
                    fitable.toUniqueId(),
                    target);
            return null;
        }
        List<Format> availableFormats = availableFormatCodes.stream()
                .map(code -> Format.custom().name(SerializationFormat.from(code).name()).code(code).build())
                .collect(Collectors.toList());
        return Target.custom()
                .workerId(target.workerId())
                .host(target.host())
                .environment(target.environment())
                .formats(availableFormats)
                .endpoints(endpoints)
                .extensions(target.extensions())
                .build();
    }

    private boolean isLocal(Target target, String localWorkerId) {
        return Objects.equals(target.workerId(), localWorkerId);
    }

    private boolean isProtocolSupported(String protocol) {
        return this.clients.stream().anyMatch(client -> client.getSupportedProtocols().contains(protocol));
    }

    private boolean isProtocolSpecified(String protocol) {
        if (this.specifiedProtocol == CommunicationProtocol.UNKNOWN) {
            return true;
        }
        return StringUtils.equalsIgnoreCase(this.specifiedProtocol.name(), protocol);
    }

    private boolean isFormatSpecified(int formatCode) {
        if (this.specifiedFormat == SerializationFormat.UNKNOWN) {
            return true;
        }
        return this.specifiedFormat.code() == formatCode;
    }
}
