/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.excelsource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fel.rag.source.ExcelSourceOptions;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * ExcelSourceServiceImplTest description}
 *
 * @since 2024-06-04
 */
public class ExcelSourceServiceImplTest {
    @Test
    void test_excel07_extract() {
        ExcelSourceServiceImpl service = new ExcelSourceServiceImpl();
        service.parseContent(ExcelSourceOptions.builder()
                .path("src/test/testfiles/test.xlsx").headRow(0).dataRow(1).sheetId(0).build());

        List<List<String>> contents = service.getSource().getContents();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).size(), 3);
    }
}
