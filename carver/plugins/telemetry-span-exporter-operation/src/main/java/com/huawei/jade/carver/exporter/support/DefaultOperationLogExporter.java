/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.support;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.carver.exporter.OperationLogExporter;
import com.huawei.jade.carver.operation.OperationLogLocaleService;
import com.huawei.jade.carver.operation.support.CompositParam;
import com.huawei.jade.carver.operation.support.OperationLogFields;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link OperationLogExporter} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@Component
public class DefaultOperationLogExporter implements OperationLogExporter {
    private static final Logger log = Logger.get(DefaultOperationLogExporter.class);
    private static final List<String> HTTPS_CONFIG_KEYS = Arrays.asList("client.http.secure.ignore-trust",
            "client.http.secure.ignore-hostname",
            "client.http.secure.trust-store-file",
            "client.http.secure.trust-store-password",
            "client.http.secure.key-store-file",
            "client.http.secure.key-store-password");

    private final HttpClassicClientFactory httpClientFactory;
    private final String collectorUri;
    private final OperationLogLocaleService operationLogLocaleService;
    private final Config config;
    private LazyLoader<HttpClassicClient> httpClient;

    public DefaultOperationLogExporter(HttpClassicClientFactory httpClientFactory,
            @Value("${collector.url}") String collectorUrl, @Fit OperationLogLocaleService operationLogLocaleService,
            Config config) {
        this.httpClientFactory = httpClientFactory;
        this.collectorUri = collectorUrl;
        this.operationLogLocaleService = operationLogLocaleService;
        this.config = config;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
    }

    @Override
    public void succeed(String operation, CompositParam params) {
        OperationLogFields fields = this.operationLogLocaleService.getLocaleMessage(operation, params);
        log.info("Operation spanned. [operation = {}]", operation);
        this.export(fields);
    }

    @Override
    public void failed(String operation, CompositParam params) {
        String errorMessage =
                params.getUserAttribute().getOrDefault(OperationLogExporter.EXCEPTION_DETAIL_KEY, StringUtils.EMPTY);
        OperationLogFields fields = this.operationLogLocaleService.getLocaleMessage(operation, params);
        log.info("Span {} error message: {}", operation, errorMessage);
        this.export(fields);
    }

    private void export(OperationLogFields fields) {
        if (StringUtils.isBlank(this.collectorUri)) {
            return;
        }
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.collectorUri);
        request.entity(Entity.createObject(request, fields));
        try (HttpClassicClientResponse<Object> response = request.exchange()) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                log.error("Failed to log POST operation. [code={}, reason={}]",
                        response.statusCode(),
                        response.reasonPhrase());
            }
        } catch (IOException e) {
            log.error("Failed to log POST operation.", e);
        }
    }

    private HttpClassicClient getHttpClient() {
        Map<String, Object> custom = HTTPS_CONFIG_KEYS.stream()
                .filter(key -> this.config.keys().contains(key))
                .collect(Collectors.toMap(key -> key, key -> this.config.get(key, Object.class)));
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder().custom(custom).build());
    }
}
