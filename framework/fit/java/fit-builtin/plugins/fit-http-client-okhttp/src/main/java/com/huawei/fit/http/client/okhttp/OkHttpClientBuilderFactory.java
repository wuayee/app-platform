/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.okhttp;

import static com.huawei.fit.http.protocol.util.SslUtils.getKeyManagers;
import static com.huawei.fit.http.protocol.util.SslUtils.getTrustManagers;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.client.http.HttpsConstants;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.protocol.util.SslUtils;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.LockUtils;
import com.huawei.fitframework.util.StringUtils;

import okhttp3.OkHttpClient;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 创建 OkHttpClient.Builder 实例工厂。
 *
 * @author 杭潇
 * @since 2024-04-15
 */
public class OkHttpClientBuilderFactory {
    private static final Logger log = Logger.get(OkHttpClientBuilderFactory.class);
    private static final String SECURE_DEFAULT_PROTOCOL = "TLSv1.2";
    private static volatile OkHttpClient.Builder okHttpClientBuilder;
    private static final Object LOCK = LockUtils.newSynchronizedLock();

    private OkHttpClientBuilderFactory() {}

    /**
     * 根据配置获取工厂实例的 {@link OkHttpClient.Builder}。
     *
     * @param config 表示配置的 {@link HttpClassicClientFactory.Config}。
     * @return 表示工厂创建实例的 {@link OkHttpClient.Builder}。
     */
    public static OkHttpClient.Builder getOkHttpClientBuilder(HttpClassicClientFactory.Config config) {
        if (okHttpClientBuilder == null) {
            synchronized (LOCK) {
                if (okHttpClientBuilder == null) {
                    okHttpClientBuilder = getClientBuilder(config);
                }
            }
        }
        return okHttpClientBuilder;
    }

    private static OkHttpClient.Builder getClientBuilder(HttpClassicClientFactory.Config config) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        try {
            setSslConfig(clientBuilder, config);
        } catch (GeneralSecurityException e) {
            log.error("Failed to set https config.");
            log.debug("Exception: ", e);
            throw new IllegalStateException("Failed to set https config.", e);
        }
        return clientBuilder;
    }

    private static void setSslConfig(OkHttpClient.Builder clientBuilder, HttpClassicClientFactory.Config config)
            throws GeneralSecurityException {
        String keyStoreFile = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_FILE));
        String keyStorePassword = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_PASSWORD));
        Boolean isStrongRandom = cast(config.custom().getOrDefault(HttpsConstants.CLIENT_SECURE_STRONG_RANDOM, false));
        String secureProtocol = cast(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_SECURITY_PROTOCOL, SECURE_DEFAULT_PROTOCOL));
        KeyManager[] keyManagers;
        if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(keyStorePassword)) {
            keyManagers = getKeyManagers(keyStoreFile, keyStorePassword);
        } else {
            keyManagers = null;
        }

        TrustManager[] trustManagers;
        boolean isIgnoreTrust = Boolean.parseBoolean(String.valueOf(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_TRUST, false)));
        if (isIgnoreTrust) {
            trustManagers = getIgnoreTrustManagers();
        } else {
            String trustStoreFile = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_FILE));
            String trustStorePassword = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_PASSWORD));
            if (StringUtils.isNotBlank(trustStoreFile) && StringUtils.isNotBlank(trustStorePassword)) {
                trustManagers = getTrustManagers(trustStoreFile, trustStorePassword);
            } else {
                trustManagers = null;
            }
        }

        SSLContext sslContext = SslUtils.getSslContext(keyManagers, trustManagers, isStrongRandom, secureProtocol);
        if (trustManagers != null && trustManagers[0] instanceof X509TrustManager) {
            clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        }
        boolean isIgnoreHostname = Boolean.parseBoolean(String.valueOf(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_HOSTNAME, false)));
        if (isIgnoreHostname) {
            clientBuilder.hostnameVerifier((hostname, session) -> true);
        }
    }

    private static TrustManager[] getIgnoreTrustManagers() {
        return new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
    }
}
