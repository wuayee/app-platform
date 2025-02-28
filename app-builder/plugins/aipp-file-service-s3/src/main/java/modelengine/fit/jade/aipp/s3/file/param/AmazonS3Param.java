/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.param;

import lombok.Data;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

/**
 * 表示 AmazonS3 客户端初始化参数。
 *
 * @author 兰宇晨
 * @since 2025-1-6
 */
@Data
@Component
public class AmazonS3Param {
    @Value("${s3.access-key}")
    private String accessKey;

    @Value("${s3.secret-key}")
    private String secretKey;

    @Value("${s3.host}")
    private String host;

    @Value("${s3.config.signer}")
    private String signer;

    @Value("${s3.config.socket-timeout}")
    private int socketTimeout;

    @Value("${s3.config.max-connections}")
    private int maxConnections;

    @Value("${s3.config.connection-timeout}")
    private int connectionTimeout;

    @Value("${s3.app-name}")
    private String appName;

    @Value("${s3.huawei-app-id}")
    private String huaweiAppId;

    @Value("${s3.bucket}")
    private String bucket;
}
