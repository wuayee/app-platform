/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.config;

import modelengine.fit.jade.aipp.code.domain.entity.support.PythonCodeExecutorProxy;
import modelengine.fit.jade.aipp.code.domain.factory.CodeExecutorFactory;
import modelengine.fit.jade.aipp.code.domain.factory.support.DefaultCodeExecutorFactory;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.serialization.ObjectSerializer;

/**
 * 表示代码执行插件的自动配置类。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
@Component
public class CodeExecutorAutoConfig {
    /**
     * 获取代码执行器工厂。
     *
     * @param brokerClient 表示动态路由器服务的 {@link BrokerClient}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示代码执行器工厂的 {@link CodeExecutorFactory}。
     */
    @Bean
    public CodeExecutorFactory getCodeExecutorFactory(BrokerClient brokerClient,
            @Fit(alias = "json") ObjectSerializer serializer) {
        CodeExecutorFactory factory = new DefaultCodeExecutorFactory();
        factory.register(new PythonCodeExecutorProxy(brokerClient, serializer));
        return factory;
    }
}