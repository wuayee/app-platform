/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.config;

import lombok.extern.slf4j.Slf4j;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * 支持忽略证书配置的 http 客户端
 *
 * @author 邬涨财
 * @since 2025/01/05
 */
@Slf4j
@Configuration
public class SslIgnoreHttpClientFactory {
    /**
     * 获取 http 客户端
     *
     * @return http 客户端
     * @throws NoSuchAlgorithmException 当没有这样算法的时候，抛出该异常
     * @throws KeyStoreException key保存失败的时候，抛出该异常
     * @throws KeyManagementException key管理失败的时候，抛出该异常
     */
    public CloseableHttpClient getHttpClient()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(SSLContextBuilder.create()
                        .setProtocol("TLSv1.2")
                        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .build())
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        PoolingHttpClientConnectionManager connManager =
                PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(sslSocketFactory).build();
        return HttpClients.custom().setConnectionManager(connManager).build();
    }
}