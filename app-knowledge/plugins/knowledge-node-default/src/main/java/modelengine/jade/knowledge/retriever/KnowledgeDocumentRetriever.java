/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.retriever;

import modelengine.jade.knowledge.KnowledgeOption;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fel.core.pattern.Retriever;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.UuidUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库向量检索。
 *
 * @author 刘信宏
 * @since 2024-09-27
 */
public class KnowledgeDocumentRetriever implements Retriever<String, MeasurableDocument> {
    private final BrokerClient brokerClient;

    private final RetrieverOption option;

    /**
     * 使用知识库服务和检索配置初始化 {@link KnowledgeDocumentRetriever} 对象。
     *
     * @param brokerClient brokerClient
     * @param option 表示检索配置的 {@link RetrieverOption}。
     */
    public KnowledgeDocumentRetriever(BrokerClient brokerClient, RetrieverOption option) {
        this.brokerClient = brokerClient;
        this.option = Validation.notNull(option, "The retriever option cannot be null.");
    }

    @Override
    public List<MeasurableDocument> retrieve(String query) {
        Validation.notNull(query, "The query cannot be null.");

        KnowledgeOption knowledgeOption = KnowledgeOption.custom()
                .query(query)
                .similarityThreshold(this.option.getSimilarityThreshold())
                .repoIds(this.option.getRepoIds())
                .referenceLimit(this.option.getReferenceLimit())
                .indexType(IndexType.from(this.option.getIndexType().type()))
                .build();
        List<KnowledgeDocument> documents = brokerClient.getRouter(KnowledgeRepoService.class,
                        KnowledgeRepoService.GENERICABLE_RETRIEVE)
                .route(new FitableIdFilter(this.option.getGroupId()))
                .invoke(this.option.getApiKey(), new FlatKnowledgeOption(knowledgeOption));
        String groupId = UuidUtils.randomUuidString();
        return documents.stream()
                .map(doc -> new MeasurableDocument(doc, doc.score(), groupId)).collect(Collectors.toList());
    }
}