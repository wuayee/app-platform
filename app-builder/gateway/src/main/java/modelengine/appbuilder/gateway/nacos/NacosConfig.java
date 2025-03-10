/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.nacos;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos 客户端的配置类。
 *
 * @author 方誉州
 * @since 2025-01-13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nacos")
public class NacosConfig {
    private String address;
    private String username;
    private String password;
    private boolean tlsEnabled;
    private boolean clientAuth;
    private String clientTrustCert;
    private String loggingPath;
}
