/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import modelengine.fit.jade.aipp.s3.file.param.AmazonS3Param;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;

/**
 * AmazonS3 客户端的自动配置类。
 *
 * @author 兰宇晨
 * @since 2024-12-26
 */
@Component
public class AmazonS3AutoConfig {
    private final AmazonS3Param amazonS3Param;

    public AmazonS3AutoConfig(AmazonS3Param amazonS3Param) {
        this.amazonS3Param = amazonS3Param;
    }

    /**
     * 将 AmazonS3 客户端注入到 S3 服务中。
     *
     * @return 表示 AmazonS3 客户端的 {@link AmazonS3}。
     */
    @Bean
    public AmazonS3 amazonS3() {
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);
        config.setSignerOverride(this.amazonS3Param.getSigner());
        config.setSocketTimeout(this.amazonS3Param.getSocketTimeout());
        config.setMaxConnections(this.amazonS3Param.getMaxConnections());
        config.setConnectionTimeout(this.amazonS3Param.getConnectionTimeout());
        AWSCredentials credentials =
                new BasicAWSCredentials(this.amazonS3Param.getAccessKey(), this.amazonS3Param.getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(config)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.amazonS3Param.getHost(),
                        Regions.US_EAST_1.getName()))
                .enablePathStyleAccess()
                .build();
    }
}
