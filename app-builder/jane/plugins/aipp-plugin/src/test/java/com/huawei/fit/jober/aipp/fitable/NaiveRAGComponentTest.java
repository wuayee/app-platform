/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jade.NaiveRAGService;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.constants.AippConst;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link NaiveRAGComponent}的测试集
 *
 * @author h00804153
 * @since 2024-04-26
 */
@ExtendWith(MockitoExtension.class)
public class NaiveRAGComponentTest {
    private static final String NAIVE_RAG_KNOWLEDGE_KEY = "knowledge";
    private static final String NAIVE_RAG_QUERY_KEY = "query";
    private static final String NAIVE_RAG_MAXIMUM_KEY = "maximum";
    private static final String NAIVE_RAG_OUTPUT = "retrievalOutput";
    private static final String DUMMY_QUERY = "This is query.";
    private static final Integer DUMMY_MAXIMUM = 3;

    @Mock
    private NaiveRAGService naiveRAGServiceMock;

    private NaiveRAGComponent naiveRAGComponent;

    @BeforeEach
    void setUp() {
        this.naiveRAGComponent = new NaiveRAGComponent(naiveRAGServiceMock);
    }

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(NAIVE_RAG_QUERY_KEY, DUMMY_QUERY);
        businessData.put(NAIVE_RAG_MAXIMUM_KEY, DUMMY_MAXIMUM);
        return businessData;
    }

    @Test
    void shouldOkWhenNoKnowledge() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put(NAIVE_RAG_OUTPUT, StringUtils.EMPTY);
        }};
        businessData.put("output", exceptResult);
        this.naiveRAGComponent.handleTask(flowData);
        verify(this.naiveRAGServiceMock, never()).process(any(), any(), any());
        Assertions.assertEquals(businessData.get("output"), exceptResult);
    }

    @Test
    void shouldOkWhenKnowledgeIsEmpty() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        List<Map<String, Object>> knowldegeList = Collections.singletonList(new HashMap<>());
        businessData.put(NAIVE_RAG_KNOWLEDGE_KEY, knowldegeList);
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put(NAIVE_RAG_OUTPUT, StringUtils.EMPTY);
        }};
        businessData.put("output", exceptResult);
        this.naiveRAGComponent.handleTask(flowData);
        verify(this.naiveRAGServiceMock, never()).process(any(), any(), any());
        Assertions.assertEquals(businessData.get("output"), exceptResult);
    }

    @Test
    void shouldOkWhenUseKnowledge() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> knowldegeList = this.getDummyKnowledgeList();
        List<String> collectionNameList = this.getDummyCollectionNameList();
        businessData.put(NAIVE_RAG_KNOWLEDGE_KEY, knowldegeList);
        String exceptNaiveRAGOutput = "This is naiveRAG result.";
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put("retrievalOutput", exceptNaiveRAGOutput);
        }};
        when(this.naiveRAGServiceMock.process(eq(DUMMY_MAXIMUM), eq(collectionNameList), eq(DUMMY_QUERY)))
                .thenReturn(exceptNaiveRAGOutput);
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        this.naiveRAGComponent.handleTask(flowData);
        verify(this.naiveRAGServiceMock, times(1)).process(DUMMY_MAXIMUM, collectionNameList, DUMMY_QUERY);
        Assertions.assertEquals(businessData.get("output"), exceptResult);
    }

    @Test
    void shouldThrowWhenConnectNaiveRAGServiceFailed() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> knowldegeList = this.getDummyKnowledgeList();
        businessData.put(NAIVE_RAG_KNOWLEDGE_KEY, knowldegeList);
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, "");
        List<String> collectionNameList = this.getDummyCollectionNameList();
        when(this.naiveRAGServiceMock.process(eq(DUMMY_MAXIMUM), eq(collectionNameList), eq(DUMMY_QUERY)))
                .thenReturn(null);
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Assertions.assertThrows(AippException.class, () -> this.naiveRAGComponent.handleTask(flowData));
    }

    @NotNull
    private List<String> getDummyCollectionNameList() {
        return new ArrayList<>(Arrays.asList("KnowledgeBase_1", "KnowledgeBase_2"));
    }

    @NotNull
    private List<Map<String, Object>> getDummyKnowledgeList() {
        return new ArrayList<>(Arrays.asList(new HashMap<String, Object>() {{
            put("id", 1);
            put("name", "knowledge1");
        }}, new HashMap<String, Object>() {{
            put("id", 2);
            put("name", "knowledge2");
        }}));
    }
}
