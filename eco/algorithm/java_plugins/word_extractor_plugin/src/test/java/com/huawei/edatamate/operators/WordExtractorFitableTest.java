/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.edatamate.operators;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import com.alibaba.fastjson.JSONObject;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * 功能描述
 *
 * @since 2024-01-24
 */
public class WordExtractorFitableTest {
    @Test
    void givenDocFileThenInvokeDocToHtmlMethod()
        throws ParserConfigurationException, IOException, TransformerException {
        String mockOutput = "mock output";
        WordExtractorFitable wordExtractorFitable = Mockito.spy(new WordExtractorFitable());
        Mockito.doReturn(mockOutput).when(wordExtractorFitable).docToHtml(any(byte[].class), any(String.class));
        List<Map<String, Object>> flowDataForDoc = flowDataConstructor("doc");
        List<Map<String, Object>> res = wordExtractorFitable.handleTask(flowDataForDoc);
        assertNotNull(res);
    }

    @Test
    void givenDocxFileThenInvokeDocToHtmlMethod() throws IOException {
        String mockOutput = "mock output";
        WordExtractorFitable wordExtractorFitable = Mockito.spy(new WordExtractorFitable());
        Mockito.doReturn(mockOutput).when(wordExtractorFitable).docxToHtml(any(byte[].class), any(String.class));
        List<Map<String, Object>> flowDataForDoc = flowDataConstructor("docx");
        List<Map<String, Object>> res = wordExtractorFitable.handleTask(flowDataForDoc);
        assertNotNull(res);
    }

    private List<Map<String, Object>> flowDataConstructor(String wordFileType) {
        // 构造入参
        List<Map<String, Object>> flowData = new ArrayList<>();

        // 构造单个flowDatum的数据结构
        Map<String, Object> flowDatum = new HashMap<>();
        JSONObject passData = new JSONObject();
        JSONObject meta = new JSONObject();

        // 填充数据
        meta.put("filePath", "mockPath/" + wordFileType);
        meta.put("fileType", wordFileType);
        meta.put("fileName", "mockFileName");
        passData.put("meta", meta);
        passData.put("data", new byte[0]);
        flowDatum.put("passData", passData);

        flowData.add(flowDatum);

        return flowData;
    }
}
