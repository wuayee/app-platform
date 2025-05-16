/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.common.oms.nacos.support;

import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.server.FitServer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.ThreadUtils;
import modelengine.jade.common.oms.config.NacosConfig;
import modelengine.jade.common.oms.nacos.NacosClient;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.tls.TlsSystemConfig;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 表示 {@link NacosClient} 的默认实现。
 *
 * @author 何天放
 * @author 李金绪
 * @since 2024-11-18
 */
@Component
public class DefaultNacosClient implements NacosClient {
    private static final Logger log = Logger.get(DefaultNacosClient.class);
    private static final String SERVICE_NAME = "app-engine-builder";
    private static final String SERVER_HOST = "app-builder";
    private static final String REGISTER_SOURCE = "preserved.register.source";
    private static final String FIT = "FIT";
    private static final String SECURE = "secure";
    private static final String HTTPS = "HTTPS";
    private static final String HTTP = "HTTP";
    private static final String PROPERTY_SERVER_ADDR = "serverAddr";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String TUNNELING_DISABLE_SCHEMES = "jdk.http.auth.tunneling.disabledSchemes";
    private static final String PROXYING_DISABLE_SCHEMES = "jdk.http.auth.proxying.disabledSchemes";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final long maxRetryTimes = 10L;
    private static final long retryInterval = 3000L;

    private final NacosConfig nacosConfig;
    private final FitServer fitServer;
    private final NamingService namingService;

    /**
     * 构造函数。
     *
     * @param nacosConfig 表示配置的 {@link NacosConfig}。
     * @param fitServer 表示 FIT 的服务器的 {@link FitServer}。
     */
    public DefaultNacosClient(NacosConfig nacosConfig, FitServer fitServer) {
        this.nacosConfig = notNull(nacosConfig, "The nacos config cannot be null.");
        this.fitServer = notNull(fitServer, "The fit server cannot be null.");
        this.setSystemProperty();
        this.namingService = this.buildNamingService();
        this.registerInstance();
    }

    @Override
    public void registerService(String serviceName, String ip, int port) {
        try {
            this.namingService.registerInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Cannot register service through nacos. [serviceName={0}, ip={1}, port={2}]",
                    serviceName,
                    ip,
                    port), e);
        }
    }

    @Override
    public void unregisterService(String serviceName, String ip, int port) {
        try {
            this.namingService.deregisterInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Cannot unregister service through nacos. [serviceName={0}, ip={1}, port={2}]",
                    serviceName,
                    ip,
                    port), e);
        }
    }

    @Override
    public List<Instance> queryService(String serviceName) {
        try {
            return this.namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            throw new IllegalStateException(StringUtils.format("Cannot query service through nacos. [serviceName={0}]",
                    serviceName), e);
        }
    }

    private Properties buildProperty(NacosConfig nacosConfig) {
        Properties properties = new Properties();
        String address = notNull(nacosConfig.getAddress(), "The nacos address cannot be blank.");
        properties.put(PROPERTY_SERVER_ADDR, address);
        if (nacosConfig.getUsername() != null && nacosConfig.getPassword() != null) {
            properties.put(PROPERTY_USERNAME, nacosConfig.getUsername());
            properties.put(PROPERTY_PASSWORD, nacosConfig.getPassword());
        }
        return properties;
    }

    private void setSystemProperty() {
        if (this.nacosConfig.isTlsEnabled()) {
            System.setProperty(TlsSystemConfig.TLS_ENABLE, TRUE);
        }
        if (this.nacosConfig.isClientAuth() && this.nacosConfig.getClientTrustCert() != null) {
            System.setProperty(TlsSystemConfig.CLIENT_AUTH, TRUE);
            System.setProperty(TlsSystemConfig.CLIENT_TRUST_CERT, this.nacosConfig.getClientTrustCert());
            System.setProperty(TUNNELING_DISABLE_SCHEMES, StringUtils.EMPTY);
            System.setProperty(PROXYING_DISABLE_SCHEMES, StringUtils.EMPTY);
        }
    }

    private NamingService buildNamingService() {
        try {
            NamingService newNamingService = NacosFactory.createNamingService(this.buildProperty(this.nacosConfig));
            log.info("Create nacos naming service successfully.");
            return newNamingService;
        } catch (NacosException e) {
            throw new IllegalStateException("Create nacos naming service failed.", e);
        }
    }

    private void registerInstance() {
        List<Instance> instances = this.buildInstances();
        notEmpty(instances, () -> new IllegalStateException("The instance list cannot be empty."));
        for (long retryTime = 0; retryTime < maxRetryTimes; retryTime++) {
            try {
                this.namingService.registerInstance(SERVICE_NAME, this.preferHttpsInstance(instances));
                log.info("Register instances successfully.");
                return;
            } catch (NacosException e) {
                log.warn("Register instances failed. [retryTime={}, error={}]", retryTime, e.getErrMsg());
                if (retryTime == maxRetryTimes - 1) {
                    throw new IllegalStateException("Register instances failed.", e);
                }
                ThreadUtils.sleep(retryInterval);
            }
        }
    }

    private Instance preferHttpsInstance(List<Instance> instances) {
        Optional<Instance> httpsInstance = instances.stream()
                .filter(instance -> StringUtils.equals(instance.getMetadata().get(SECURE), TRUE))
                .findFirst();
        return httpsInstance.orElseGet(() -> instances.get(0));
    }

    private List<Instance> buildInstances() {
        return this.fitServer.endpoints()
                .stream()
                .map(this::createInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Instance> createInstance(Endpoint endpoint) {
        Instance instance = new Instance();
        instance.setIp(SERVER_HOST);
        instance.setPort(endpoint.port());
        instance.addMetadata(REGISTER_SOURCE, FIT);
        if (StringUtils.equalsIgnoreCase(endpoint.protocol(), HTTPS)) {
            instance.addMetadata(SECURE, TRUE);
        } else if (StringUtils.equalsIgnoreCase(endpoint.protocol(), HTTP)) {
            instance.addMetadata(SECURE, FALSE);
        } else {
            return Optional.empty();
        }
        return Optional.of(instance);
    }
}