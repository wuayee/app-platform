package com.huawei.fit.jade.pattern;

import static com.huawei.jade.fel.rag.store.connector.JdbcType.POSTGRESQL;

import com.huawei.fit.jade.MilvusVectorConnector;
import com.huawei.fit.jade.NaiveRAGService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.rag.retrieve.MultiSourceRetriever;
import com.huawei.jade.fel.rag.store.connector.ConnectorProperties;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;

import java.util.List;

@Component
public class LocalNaiveRAGService implements NaiveRAGService {
    @Override
    @Fitable(id = "localSync")
    public String process(Integer topK, List<String> collectionName, String question) {
        MilvusVectorConnector milvusConn;
        JdbcSqlConnector jdbcConn;
        ConnectorProperties properties =
                new ConnectorProperties("80.11.128.66", 5433, "postgres", "postgres");

        try {
            milvusConn = new MilvusVectorConnector("80.11.128.62", 19530, null, null, null);
            jdbcConn = new JdbcSqlConnector(POSTGRESQL, properties, "ai_edm_backend");
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
            return null;
        }

        Retriever<String, String> retriever = new MultiSourceRetriever(collectionName, topK, milvusConn, jdbcConn);
        AiProcessFlow<String, String> flow = AiFlows.<String>create()
                .retrieve(retriever)
                .close();

        return flow.converse().offer(question).await();
    }
}
