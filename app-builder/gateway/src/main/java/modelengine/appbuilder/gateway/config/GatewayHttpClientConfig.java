/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.config;

import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.utils.SslUtil;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.gateway.config.HttpClientFactory;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Collections;

import javax.net.ssl.SSLException;

/**
 * 为 gateway 提供支持证书校验的http客户端
 *
 * @author 李智超
 * @since 2024/08/29
 */
@Configuration
@Slf4j
public class GatewayHttpClientConfig {
    @Value("${httpClient.ssl.common-password}")
    private String encryptedPwd;

    @Value("${httpClient.ssl.trust-store}")
    private Resource trustStore;

    @Value("${httpClient.ssl.key-store}")
    private Resource keyStore;

    /**
     * 网关 http 客户端
     *
     * @param properties {@link HttpClientProperties}
     * @param serverProperties {@link ServerProperties}
     * @return {@link HttpClientFactory}
     */
    @Bean
    @ConditionalOnMissingBean({HttpClient.class, HttpClientFactory.class})
    public HttpClientFactory gatewayHttpClientFactory(HttpClientProperties properties,
            ServerProperties serverProperties) {
        return new CustomHttpClientFactory(properties,
                serverProperties,
                this.encryptedPwd,
                this.trustStore,
                this.keyStore);
    }

    private static class CustomHttpClientFactory extends HttpClientFactory {
        private static final String[] SUPPORTED_TLS_PROTOCOLS = {"TLSv1.3", "TLSv1.2"};

        private final String encryptedPwd;
        private final Resource trustStore;
        private final Resource keyStore;

        /**
         * CustomHttpClientFactory 构造方法
         *
         * @param properties {@link HttpClientProperties}
         * @param serverProperties {@link ServerProperties}
         * @param encryptedPwd 证书密码
         * @param trustStore 信任证书
         * @param keyStore 证书密钥
         */
        public CustomHttpClientFactory(HttpClientProperties properties, ServerProperties serverProperties,
                String encryptedPwd, Resource trustStore, Resource keyStore) {
            super(properties, serverProperties, Collections.emptyList());
            this.encryptedPwd = encryptedPwd;
            this.trustStore = trustStore;
            this.keyStore = keyStore;
        }

        /**
         * 设置 ssl 上下文
         *
         * @param httpClient 网关 http 客户端
         * @return 网关 http 客户端
         */
        protected HttpClient configureSsl(HttpClient httpClient) {
            HttpClientProperties.Ssl ssl = properties.getSsl();
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
            try {
                sslContextBuilder.trustManager(SslUtil.getTrustManagers(this.trustStore.getFile().getAbsolutePath(),
                        this.encryptedPwd)[0]);
                sslContextBuilder.keyManager(SslUtil.getKeyManagers(this.keyStore.getFile().getAbsolutePath(),
                        this.encryptedPwd)[0]);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Failed to find cert files.");
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException e) {
                throw new IllegalStateException("Failed to find configure gateway ssl context.");
            }
            sslContextBuilder.protocols(SUPPORTED_TLS_PROTOCOLS);
            SslProvider sslProvider;
            try {
                sslProvider = SslProvider.builder()
                        .sslContext(sslContextBuilder.build())
                        .handshakeTimeout(ssl.getHandshakeTimeout())
                        .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                        .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout())
                        .build();
            } catch (SSLException e) {
                throw new IllegalStateException("Failed to config ssl: " + e.getMessage());
            }
            return httpClient.secure(sslProvider);
        }
    }
}
