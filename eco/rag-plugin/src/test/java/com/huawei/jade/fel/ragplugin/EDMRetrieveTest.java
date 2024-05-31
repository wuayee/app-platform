package com.huawei.jade.fel.ragplugin;

import com.huawei.jade.fel.rag.retrieve.MultiSourceRetriever;
import com.huawei.jade.fel.rag.store.connector.ConnectorProperties;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;
import com.huawei.fit.jade.MilvusVectorConnector;
import com.huawei.jade.fel.rag.store.connector.SqlConnector;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.huawei.jade.fel.rag.store.connector.JdbcType.POSTGRESQL;

public class EDMRetrieveTest {
    @Test
    @Disabled("环境强依赖，不可以放在单测中")
    void testMultiSourceRetriever() {
        MilvusVectorConnector milvusConn;
        SqlConnector jdbcConn;
        ConnectorProperties properties =
                new ConnectorProperties("80.11.128.66", 5433, "postgres", "postgres");

        try {
            milvusConn = new MilvusVectorConnector("80.11.128.62", 19530, null, null, null);
            jdbcConn = new JdbcSqlConnector(POSTGRESQL, properties, "ai_edm_backend");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MultiSourceRetriever retriever = new MultiSourceRetriever(Arrays.asList("KnowledgeBase_96"), 1, milvusConn, jdbcConn);
        System.out.println(retriever.invoke("山东代表处"));
    }
}