/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.infra.config;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fit.jade.aipp.rewrite.domain.entity.support.BuiltinRewriter;
import modelengine.fit.jade.aipp.rewrite.domain.entity.support.CustomRewriter;
import modelengine.fit.jade.aipp.rewrite.domain.factory.RewriterFactory;
import modelengine.fit.jade.aipp.rewrite.domain.factory.support.DefaultRewriterFactory;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.io.IOException;

/**
 * 表示重写插件的自动配置类。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
@Component
public class RewriteAutoConfig {
    /**
     * 获取重写算子工厂。
     *
     * @param modelService 表示模型服务的 {@link ChatModel}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示重写算子工厂的 {@link RewriterFactory}。
     * @throws IOException 当读取默认提示词文件失败时。
     */
    @Bean
    public RewriterFactory getRewriteFactory(ChatModel modelService,
            @Fit(alias = "json") ObjectSerializer serializer) throws IOException {
        RewriterFactory factory = new DefaultRewriterFactory();
        factory.register(new CustomRewriter(modelService));
        factory.register(new BuiltinRewriter(modelService, serializer));
        return factory;
    }
}