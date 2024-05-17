package com.huawei.jade.fel.rag;

import com.huawei.jade.fel.engine.operators.sources.Source;
import com.huawei.jade.fel.rag.common.Document;
import com.huawei.jade.fel.rag.source.ExcelSource;
import org.junit.jupiter.api.Test;

public class ExcelSourceTest {
    @Test
    void test_excel07_extract() {
        ExcelSource source = new ExcelSource();
        source.load("src/test/testfiles/test.xlsx", 0, 1, 0);
    }

    @Test
    void test_excel03_extract() {
        ExcelSource source = new ExcelSource();
        source.load("src/test/testfiles/test03.xls", 0, 1, 0);
    }
}
