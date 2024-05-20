package com.huawei.fit.jade.pattern;

import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.MessageContent;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.rag.retrieve.MultiSourceRetriever;
import com.huawei.jade.fel.rag.store.connector.ConnectorProperties;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;
import com.huawei.fit.jade.MilvusVectorConnector;

import com.huawei.fit.jade.NaiveRAGService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.io.IOException;
import java.util.List;

import static com.huawei.jade.fel.rag.store.connector.JdbcType.POSTGRESQL;

@Component
public class LocalNaiveRAGService implements NaiveRAGService {
    @Override
    @Fitable(id = "localSync")
    public String process(Integer topK, List<String> collectionName, String question) {
        MilvusVectorConnector milvusConn;
        JdbcSqlConnector jdbcConn;
        ConnectorProperties properties =
                new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");

        try {
            milvusConn = new MilvusVectorConnector("51.36.139.24", 19530, null, null, null);
            jdbcConn = new JdbcSqlConnector(POSTGRESQL, properties, "ai_edm_backend");
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
            return null;
        }

        Retriever<String> retriever = new MultiSourceRetriever(collectionName, topK, milvusConn, jdbcConn);
        AiProcessFlow<String, MessageContent> flow = AiFlows.<String>create()
                .retrieve(retriever)
                .close();

        return flow.converse().offer(question).await().text();
    }
}
