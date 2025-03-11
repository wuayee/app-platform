/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.nacos;

import static org.apache.commons.lang3.Validate.notNull;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.tls.TlsSystemConfig;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * 默认的 Nacos 客户端实现。
 *
 * @author 方誉州
 * @since 2025-01-13
 */
@Slf4j
@Component
public class DefaultNacosClient {
    private static final String SERVICE_NAME = "app-engine-gateway";
    private static final String SERVER_HOST = "app-builder-gateway";
    private static final String REGISTER_SOURCE = "preserved.register.source";
    private static final String FIT = "FIT";
    private static final String SECURE = "secure";
    private static final String PROPERTY_SERVER_ADDR = "serverAddr";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String TUNNELING_DISABLE_SCHEMES = "jdk.http.auth.tunneling.disabledSchemes";
    private static final String PROXYING_DISABLE_SCHEMES = "jdk.http.auth.proxying.disabledSchemes";
    private static final String NACOS_LOGGING_PATH_SCHEMES = "nacos.logging.path";
    private static final String TRUE = "true";
    private static final long MAX_RETRY_TIMES = 10L;
    private static final long RETRY_INTERVAL = 3000L;
    private static final String EMPTY = "";

    private final NacosConfig nacosConfig;
    private final NamingService namingService;

    @Value("${server.port}")
    private int serverPort;

    /**
     * 构造函数。
     *
     * @param nacosConfig 表示配置的 {@link NacosConfig}。
     * @throws InterruptedException 线程等待出现异常时抛出该异常。
     */
    public DefaultNacosClient(NacosConfig nacosConfig) throws InterruptedException {
        this.nacosConfig = notNull(nacosConfig, "The nacos config cannot be null.");
        this.setSystemProperty();
        this.namingService = this.buildNamingService();
        this.registerInstance();
    }

    /**
     * 注册服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @param ip 表示服务所在主机的 {@link String}。
     * @param port 表示服务所在端口的 {@code int}。
     */
    public void registerService(String serviceName, String ip, int port) {
        try {
            this.namingService.registerInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Cannot register service through nacos. [serviceName=%s, ip=%s, port=%d]",
                    serviceName,
                    ip,
                    port), e);
        }
    }

    /**
     * 取消注册服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @param ip 表示服务所在主机的 {@link String}。
     * @param port 表示服务所在端口的 {@code int}。
     */
    public void unregisterService(String serviceName, String ip, int port) {
        try {
            this.namingService.deregisterInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Cannot unregister service through nacos. [serviceName=%s, ip=%s, port=%d]",
                    serviceName,
                    ip,
                    port), e);
        }
    }

    /**
     * 查询服务。
     *
     * @param serviceName 表示服务名称的 {@link String}。
     * @return 返回服务实例的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    public List<Instance> queryService(String serviceName) {
        try {
            return this.namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Cannot query service through nacos. [serviceName=%s]",
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
        System.setProperty(NACOS_LOGGING_PATH_SCHEMES, this.nacosConfig.getLoggingPath());
        if (this.nacosConfig.isTlsEnabled()) {
            System.setProperty(TlsSystemConfig.TLS_ENABLE, TRUE);
        }
        if (this.nacosConfig.isClientAuth() && this.nacosConfig.getClientTrustCert() != null) {
            System.setProperty(TlsSystemConfig.CLIENT_AUTH, TRUE);
            System.setProperty(TlsSystemConfig.CLIENT_TRUST_CERT, this.nacosConfig.getClientTrustCert());
            System.setProperty(TUNNELING_DISABLE_SCHEMES, EMPTY);
            System.setProperty(PROXYING_DISABLE_SCHEMES, EMPTY);
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

    private void registerInstance() throws InterruptedException {
        for (long retryTime = 0; retryTime < MAX_RETRY_TIMES; retryTime++) {
            try {
                this.namingService.registerInstance(SERVICE_NAME, this.createInstance());
                log.info("Register instances successfully.");
                return;
            } catch (NacosException e) {
                log.warn("Register instances failed. [retryTime={}, error={}]", retryTime, e.getErrMsg());
                if (retryTime == MAX_RETRY_TIMES - 1) {
                    throw new IllegalStateException("Register instances failed.", e);
                }
                Thread.sleep(RETRY_INTERVAL);
            }
        }
    }

    private Instance createInstance() {
        Instance instance = new Instance();
        instance.setIp(SERVER_HOST);
        instance.setPort(this.serverPort);
        instance.addMetadata(REGISTER_SOURCE, FIT);
        instance.addMetadata(SECURE, TRUE);
        return instance;
    }
}
