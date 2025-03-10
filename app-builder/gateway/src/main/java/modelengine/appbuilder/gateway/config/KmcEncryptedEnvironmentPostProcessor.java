/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.config;

import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.kmc.KmcDecryptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 利用SCC 解密工具对加密的环境变量进行解密
 *
 * @author 李智超
 * @since 2024/7/30
 */
@Order
@Component
@Slf4j
public class KmcEncryptedEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Map<String, String> keyMaps = new HashMap<>();

    static {
        keyMaps.put("SERVER_SSL_KEY_STORE_PASSWORD", "server.ssl.key-store-password");
        keyMaps.put("SERVER_SSL_KEY_PASSWORD", "server.ssl.key-password");
        keyMaps.put("SERVER_SSL_TRUST_STORE_PASSWORD", "server.ssl.trust-store-password");
        keyMaps.put("HTTPCLIENT_SSL_COMMON_PASSWORD", "httpClient.ssl.common-password");
    }

    private final KmcDecryptor kmcDecryptor = new KmcDecryptor();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> decryptedProperties = new HashMap<>();
        BiConsumer<String, Object> decryptConsumer = (key, value) -> {
            if (keyMaps.containsKey(key)) {
                String valueStr = String.valueOf(value);
                decryptedProperties.put(getMappedKey(key), kmcDecryptor.decrypt(valueStr));
            }
        };
        environment.getSystemEnvironment().forEach(decryptConsumer);
        environment.getSystemProperties().forEach(decryptConsumer);
        MapPropertySource decryptedSource = new MapPropertySource("decryptedSource", decryptedProperties);
        environment.getPropertySources().addFirst(decryptedSource);
    }

    private static String getMappedKey(String key) {
        return keyMaps.getOrDefault(key, key);
    }
}