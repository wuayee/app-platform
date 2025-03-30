/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.retriever.support;

import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.retriever.KnowledgeDocumentRetriever;
import modelengine.jade.knowledge.retriever.RetrieverHandler;

import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Nonnull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检索处理器的默认实现。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
@Component
public class DefaultRetrieverHandler implements RetrieverHandler {
    private final BrokerClient brokerClient;

    /**
     * 使用知识库服务初始化 {@link DefaultRetrieverHandler} 对象。
     *
     * @param brokerClient brokerClient
     */
    public DefaultRetrieverHandler(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public List<MeasurableDocument> handle(@Nonnull List<String> query, @Nonnull RetrieverOption option) {
        KnowledgeDocumentRetriever retriever = new KnowledgeDocumentRetriever(brokerClient, option);
        return query.stream()
                .flatMap(input -> retriever.retrieve(input).stream())
                .collect(Collectors.toList());
    }
}
