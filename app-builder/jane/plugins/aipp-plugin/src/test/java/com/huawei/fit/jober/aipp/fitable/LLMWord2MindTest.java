/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.FileRspDto;
import com.huawei.fit.jober.aipp.dummy.OperationContextDummy;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LLMWord2MindTest {
    private LLMWord2Mind llmFitable;

    @Mock
    private LLMService llmServiceMock;

    @Mock
    private OperatorService operatorServiceMock;

    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    @Mock
    private AippLogService aippLogServiceMock;

    @BeforeEach
    void setUp() {
        this.llmFitable =
                new LLMWord2Mind(llmServiceMock, metaInstanceServiceMock, operatorServiceMock, aippLogServiceMock);
    }

    @Test
    @Disabled
    void shouldSendExpectRequestWhenCallHandleTask() throws IOException {
        // given
        final String dummyContent = "{\"title\":\"some random Content\"}";
        final String dummyId = "someRandomId";
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_FILE_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        when(operatorServiceMock.outlineExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
                eq(OperatorService.FileType.WORD))).thenReturn(dummyContent);
        when(llmServiceMock.askModelWithText(anyString(), anyInt(), anyDouble(), any())).thenReturn(dummyContent);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        List<Map<String, Object>> result = llmFitable.handleTask(flowData);
        Assertions.assertEquals(Utils.getBusiness(result).get(AippConst.INST_WORD2MIND_KEY).toString(), dummyContent);
        verify(operatorServiceMock, times(1)).outlineExtractor(any(), any());
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_WORD2MIND_KEY).equals(dummyContent)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
    }

    @Test
    void shouldThrowExceptionWhenCallHandleTaskWithExtractionFail() {
        // given
        final String dummyContent = "";
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_FILE_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));

        when(operatorServiceMock.outlineExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
                eq(OperatorService.FileType.WORD))).thenReturn(dummyContent);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(flowData));
        verify(operatorServiceMock, times(1)).outlineExtractor(any(), any());
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenCallHandleTaskWithLLMFail() throws IOException {
        // given
        final String dummyContent = "some random Content";
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_FILE_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));

        when(operatorServiceMock.outlineExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
                eq(OperatorService.FileType.WORD))).thenReturn(dummyContent);
        when(llmServiceMock.askModelWithText(anyString(), anyInt(), anyDouble(), any())).thenThrow(new IOException());

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(flowData));
        verify(operatorServiceMock, times(1)).outlineExtractor(any(), any());
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }

    @Test
    void shouldTrimToJsonWhenCallHandleTaskWithLLMReturnBadFormat() throws IOException {
        // given
        final String dummyContent = "{\"title\":\"some random Content\"}";
        final String badFormatLlmResult = String.format("```json\n%s```", dummyContent);
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_FILE_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));

        when(operatorServiceMock.outlineExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
                eq(OperatorService.FileType.WORD))).thenReturn(badFormatLlmResult);
        when(llmServiceMock.askModelWithText(anyString(), anyInt(), anyDouble(), any())).thenThrow(new IOException());

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(flowData));
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }
}
