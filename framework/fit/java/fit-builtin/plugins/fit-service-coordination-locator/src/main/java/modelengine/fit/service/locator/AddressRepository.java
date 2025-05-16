/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.locator;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.server.FitServer;
import modelengine.fit.service.RegistryLocator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.conf.runtime.MatataConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用于存储注册中心的地址。
 *
 * @author 季聿阶
 * @since 2021-04-06
 */
@Component
public class AddressRepository implements RegistryLocator {
    private static final Logger log = Logger.get(AddressRepository.class);

    private final Target registryTarget;

    /**
     * 创建存储注册中心地址的对象。
     *
     * @param servers 表示 FIT 的服务器的列表的 {@link List}{@code <}{@link FitServer}{@code >}。
     * @param worker 表示进程配置的 {@link WorkerConfig}。
     * @param matata 表示 matata 配置的 {@link MatataConfig}。
     */
    public AddressRepository(List<FitServer> servers, WorkerConfig worker, MatataConfig matata) {
        List<FitServer> actualServers = ObjectUtils.getIfNull(servers, Collections::emptyList);
        notNull(worker, "The worker config cannot be null.");
        notNull(matata, "The matata config cannot be null.");
        boolean isRegistryLocalhost = isRegistryLocalhost(actualServers,
                worker.host(),
                worker.domain(),
                matata.registry().host(),
                matata.registry().port(),
                matata.registry().protocolCode());
        String registryWorkerId =
                isRegistryLocalhost ? worker.id() : matata.registry().host() + ":" + matata.registry().port();
        this.registryTarget = Target.custom()
                .workerId(registryWorkerId)
                .host(matata.registry().host())
                .endpoints(Collections.singletonList(Endpoint.custom()
                        .port(matata.registry().port())
                        .protocol(matata.registry().protocol().name(), matata.registry().protocolCode())
                        .build()))
                .environment(matata.registry().environment())
                .extensions(matata.registry().visualExtensions())
                .build();
        log.debug("Registry location is {}.", this.registryTarget);
    }

    private static boolean isRegistryLocalhost(List<FitServer> servers, String localHost, String localDomain,
            String registryHost, int registryPort, int registryProtocol) {
        if (!isRegistryHost(localHost, localDomain, registryHost)) {
            return false;
        }
        return servers.stream()
                .filter(Objects::nonNull)
                .map(FitServer::endpoints)
                .flatMap(List::stream)
                .anyMatch(endpoint -> endpoint.port() == registryPort && endpoint.protocolCode() == registryProtocol);
    }

    private static boolean isRegistryHost(String localHost, String localDomain, String registryHost) {
        return StringUtils.equalsIgnoreCase(localHost, registryHost) || StringUtils.equalsIgnoreCase(localDomain,
                registryHost);
    }

    @Override
    public List<Target> targets() {
        return Collections.singletonList(this.registryTarget);
    }
}
