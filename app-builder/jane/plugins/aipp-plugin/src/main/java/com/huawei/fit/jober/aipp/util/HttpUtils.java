/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

/**
 * Http 操作工具类
 *
 * @author l00801120
 * @since 2024-01-15
 */
public class HttpUtils {
    /**
     * poolingClient 具备连接池和线程回收空闲链接, 支持http/https请求
     * 进程单例，不允许在try语句中关闭
     */
    private static final CloseableHttpClient POOLING_CLIENT = createHttpClient();
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
    private static final int SESSION_CACHE_SIZE = 10; // SSLSession缓存个数
    private static final int SESSION_TIMEOUT = 180; // SSLSession缓存超时
    private static final int MAX_CONNECTION_COUNT = 1000; // 最大连接数1000
    private static final int MAX_PER_ROUTE = 150; // 同路由并发数150
    private static final int CONNECTION_TIMEOUT = 5000; // 连接超时
    private static final int READ_TIMEOUT = 35000; // 数据读取超时时间30秒
    private static final int WAIT_CONNECTION_TIMEOUT = 5000; // 连接不够用的等待时间
    private static final int RETRY_COUNT = 2; // 重试次数
    private static final int MAX_IDLE_TIME = 180; // 连接空闲时间

    /**
     * 执行任务
     *
     * @param request 请求
     * @return 响应
     * @throws IOException IO异常
     */
    public static CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        return POOLING_CLIENT.execute(request);
    }

    private static CloseableHttpClient createHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            sslContext.getClientSessionContext().setSessionCacheSize(SESSION_CACHE_SIZE);
            sslContext.getClientSessionContext().setSessionTimeout(SESSION_TIMEOUT);
            sslContext.getServerSessionContext().setSessionCacheSize(SESSION_CACHE_SIZE);
            sslContext.getServerSessionContext().setSessionTimeout(SESSION_TIMEOUT);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                    .build();

            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                    new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolingHttpClientConnectionManager.setMaxTotal(MAX_CONNECTION_COUNT);
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(RETRY_COUNT, true));

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(READ_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(WAIT_CONNECTION_TIMEOUT)
                    .build();

            httpClientBuilder.setDefaultRequestConfig(requestConfig)
                    .disableConnectionState()
                    .evictIdleConnections(MAX_IDLE_TIME, TimeUnit.SECONDS)
                    .evictExpiredConnections();
            return httpClientBuilder.build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error("create PoolingHttpClient fail.", e);
        }
        return null;
    }

    /**
     * 发送 http 请求
     *
     * @param httpRequest http 请求
     * @return http 回复
     * @throws IOException 当 http 请求发送失败时，抛出该 IO 异常
     */
    public static String sendHttpRequest(HttpRequestBase httpRequest) throws IOException {
        try (CloseableHttpResponse response = HttpUtils.execute(httpRequest)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        httpRequest.getURI(),
                        response.getStatusLine().getStatusCode()));
            }
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取HttpClient 请求配置
     *
     * @param socketTimeout 读取内存超时时长
     * @return 请求配置
     */
    public static RequestConfig requestConfig(int socketTimeout) {
        final int connectTimeout = 5000;
        final int connectRequestTimeout = 5000;
        return RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectRequestTimeout)
                .build();
    }
}
