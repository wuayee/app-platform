/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.service;

import com.huawei.framework.crypt.grpc.client.CryptClient;
import com.huawei.framework.crypt.grpc.client.config.GlobalConfig;
import com.huawei.framework.crypt.grpc.client.utils.PassUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 * @author 李智超
 * @since 2025-01-02
 */
@Slf4j
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
        log.info("Start init CryptClient");
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
        } catch (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException |
                 KeyStoreException e) {
            throw new IllegalStateException("Load kms client error", e);
        }
    }
}

