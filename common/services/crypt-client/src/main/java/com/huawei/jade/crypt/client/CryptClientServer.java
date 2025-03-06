/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.crypt.client;

import com.huawei.framework.crypt.grpc.client.CryptClient;
import com.huawei.framework.crypt.grpc.client.config.GlobalConfig;
import com.huawei.framework.crypt.grpc.client.utils.PassUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;

/**
 * CryptClient 初始化类。
 *
 * @author 杭潇
 * @since 2024-12-30
 */
@NoArgsConstructor
public final class CryptClientServer {
    private static final String PLATFORM_FRAMEWORK_JKS = "platform_framework_jks";
    private static final String SECURITY_PRIV_TOMCAT_CONF = "trustCertCollectionFilePass";
    private static final String APPLICATION_PROPERTIES = "crypto_common.properties";

    /**
     * 获取加解密的客户端。
     */
    @Getter
    private static CryptClient cryptClient;

    static {
        try (InputStream inputStream = CryptClientServer.class.getClassLoader()
                .getResourceAsStream(APPLICATION_PROPERTIES)) {
            final Properties properties = new Properties();
            properties.load(inputStream);

            byte[] bytesJks =
                    FileUtils.readFileToByteArray(FileUtils.getFile(properties.getProperty(PLATFORM_FRAMEWORK_JKS)));
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] pass = PassUtils.decryptByFile(properties.getProperty(SECURITY_PRIV_TOMCAT_CONF), "platform")
                    .toCharArray();
            ks.load(new ByteArrayInputStream(bytesJks), pass);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(ks, pass);

            GlobalConfig.sharedInstance().setKeyManager(keyManagerFactory.getKeyManagers()[0]);
            GlobalConfig.sharedInstance().loadConfig(properties);
            cryptClient = CryptClient.defaultClient();
        } catch (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException
                 | KeyStoreException e) {
            throw new IllegalStateException("Load kms client error", e);
        }
    }
}

