package com.huawei.jade.fel.rag;

import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.Document;
import com.huawei.jade.fel.rag.index.TableIndex;
import com.huawei.jade.fel.rag.source.ExcelSource;
import com.huawei.jade.fel.rag.split.TableSplitter;
import com.huawei.jade.fel.rag.store.connector.ConnectorProperties;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;
import com.huawei.jade.fel.rag.store.connector.JdbcType;
import com.huawei.jade.fel.rag.store.connector.schema.DbFieldType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ExcelSourceTest {
    @Test
    void test_excel07_extract() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/test.xlsx", 0, 1, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).size(), 3);
    }

    @Test
    void test_excel03_extract() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/test03.xls", 0, 1, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).size(), 3);
    }

    @Test
    void test_extract_synonyms() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/近义词test.xlsx", 0, 0, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 5);
        assertEquals(contents.get(0).size(), 2);
    }

    @Test
    void test_extract_relation_enums() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/从属枚举test.xlsx", 0, 0, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 7);
        assertEquals(contents.get(0).size(), 2);
    }

    @Disabled
    @Test
    void test_extract_and_store() {
        ConnectorProperties prop = new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        ExcelSource source = new ExcelSource();
        List<DbFieldType> type = Arrays.asList(DbFieldType.NUMBER, DbFieldType.VARCHAR, DbFieldType.VARCHAR);
        TableIndex idx = new TableIndex(conn, type, "test");
        TableSplitter splitter = new TableSplitter();
        CountDownLatch latch = new CountDownLatch(1);

        AiProcessFlow<List<Document>, List<Chunk>> flow = AiFlows.<List<Document>>create()
                .split(splitter)
                .index(idx)
                .close(n -> {
                    latch.countDown();
                });
        flow.offer(source);
        source.load("src/test/testfiles/test.xlsx", 0, 1, 0);
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail();
        }
    }
}
