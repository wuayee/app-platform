/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dummy.OperationContextDummy;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.hllm.model.LlmModel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LLMText2TextTest {
    private static final int HLLM_READ_TIMEOUT = 150000;
    private LLMText2Text llmFitable;

    @Mock
    private LLMService llmServiceMock;

    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    @Mock
    private AippLogService aippLogServiceMock;

    @Mock
    private DistributedMapService mapServiceMock;

    @BeforeEach
    void setUp() {
        this.llmFitable = new LLMText2Text(llmServiceMock,
                metaInstanceServiceMock,
                aippLogServiceMock,
                mapServiceMock,
                HLLM_READ_TIMEOUT);
    }

    @Test
    void shouldSendExpectRequestWhenCallHandleTaskWithXiaoHaiName() throws IOException {
        final String dummyPrompt = "are you ok?";
        final String dummyModel = "XiaoHai";
        final String dummyId = "someRandomId";
        final String dummyResult = "Hello. Thank you. Thank you very much.";
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_MODEL_NAME_KEY, dummyModel);
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);
        when(llmServiceMock.askXiaoHaiKnowledge(eq(OperationContextDummy.DUMMY_W3_ACCOUNT),
                eq(dummyPrompt))).thenReturn(dummyResult);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, dummyPrompt, false);
        llmFitable.handleTask(flowData);
        verify(llmServiceMock, times(1)).askXiaoHaiKnowledge(eq(OperationContextDummy.DUMMY_W3_ACCOUNT),
                eq(dummyPrompt));
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_TEXT2TEXT_KEY).equals(dummyResult)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
    }

    @Test
    void shouldSendExpectRequestWhenCallHandleTaskWithOtherModelName() throws IOException {
        final String dummyPrompt = "are you ok?";
        final String dummyModel = "QWen_14b";
        final String dummyId = "someRandomId";
        final String dummyResult = "Hello. Thank you. Thank you very much.";
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_MODEL_NAME_KEY, dummyModel);
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);
        when(llmServiceMock.askModelWithText(eq(dummyPrompt), eq(LlmModel.QWEN_14B))).thenReturn(dummyResult);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, dummyPrompt, false);
        llmFitable.handleTask(flowData);
        verify(llmServiceMock, times(1)).askModelWithText(any(), any());
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_TEXT2TEXT_KEY).equals(dummyResult)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
    }

    @Test
    void shouldThrowWhenCallHandleTaskWithXiaoHaiNameFail() throws IOException {
        final String dummyPrompt = "are you ok?";
        final String dummyModel = "XiaoHai";
        final int ASK_MODEL_MAX_RETRY_TIMES = 3;
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_MODEL_NAME_KEY, dummyModel);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, dummyPrompt, false);
        when(llmServiceMock.askXiaoHaiKnowledge(eq(OperationContextDummy.DUMMY_W3_ACCOUNT), eq(dummyPrompt))).thenThrow(
                new IOException());

        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(flowData));
        verify(llmServiceMock,
                times(ASK_MODEL_MAX_RETRY_TIMES)).askXiaoHaiKnowledge(eq(OperationContextDummy.DUMMY_W3_ACCOUNT),
                eq(dummyPrompt));
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }
}
