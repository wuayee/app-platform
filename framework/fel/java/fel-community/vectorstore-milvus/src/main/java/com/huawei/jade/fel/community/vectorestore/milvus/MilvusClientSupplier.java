/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanSupplier;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.community.vectorestore.milvus.config.MilvusClientConfig;

import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;

import javax.annotation.PreDestroy;

/**
 * 表示 {@link MilvusClient} 的创建工厂。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
@Component
public class MilvusClientSupplier implements BeanSupplier<MilvusClient> {
    private final MilvusClient milvusClient;

    /**
     * 根据配置创建 milvus 客户端工厂。
     *
     * @param config 表示 milvus 客户端配置的 {@link MilvusClientConfig}。
     * @throws IllegalArgumentException 当 {@code config} 为 {@code null} 时。
     */
    public MilvusClientSupplier(MilvusClientConfig config) {
        notNull(config, "The config cannot be null.");
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(config.getHost())
                .withPort(config.getPort())
                .withDatabaseName(config.getDatabaseName());
        if (StringUtils.isNotBlank(config.getToken())) {
            builder.withToken(config.getToken());
        }
        this.milvusClient = new MilvusServiceClient(builder.build());
        this.milvusClient.setLogLevel(config.getLogLevel());
    }

    /**
     * 销毁 {@link MilvusClient} 实例。
     *
     * <p>在对象销毁时，需要关闭 {@link MilvusClient} 实例以释放资源。
     */
    @PreDestroy
    public void destroy() {
        this.milvusClient.close();
    }

    @Override
    public MilvusClient get() {
        return this.milvusClient;
    }
}
