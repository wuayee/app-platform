package com.huawei.jade.fel.rag;

import com.huawei.jade.fel.rag.source.ExcelSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void test_extract_synonyms_limit_row() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/近义词test.xlsx", 0, 0, 1, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 3);
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

    @Test
    void test_extract_relation_enums_limit_row() {
        ExcelSource source = new ExcelSource();
        source.parseContent("src/test/testfiles/test.xlsx", 0, 1, 1, 0);

        List<List<String>> contents = source.getContents();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).size(), 3);
    }
}
