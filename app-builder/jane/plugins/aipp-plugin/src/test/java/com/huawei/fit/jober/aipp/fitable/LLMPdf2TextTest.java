/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
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
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.FileRspDto;
import com.huawei.fit.jober.aipp.dummy.OperationContextDummy;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LLMPdf2TextTest {
    private LLMPdf2Text llmFitable;

    @Mock
    private OperatorService operatorServiceMock;

    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    @Mock
    private AippLogService aippLogServiceMock;

    @BeforeEach
    void setUp() {
        this.llmFitable = new LLMPdf2Text(operatorServiceMock, metaInstanceServiceMock, aippLogServiceMock);
    }

    @Test
    void shouldSendExpectRequestWhenCallHandleTask() {
        // given
        final String dummyPdfContent = "some random Content";
        final String dummyId = "someRandomId";
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_PDF_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        when(operatorServiceMock.fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.PDF)))).thenReturn(dummyPdfContent);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        llmFitable.handleTask(flowData);

        verify(operatorServiceMock, times(1)).fileExtractor(any(), any());
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_TEXT2TEXT_KEY).equals(dummyPdfContent)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
    }

    @Test
    void shouldThrowExceptionWhenCallHandleTaskWithExtractionFail() {
        // given
        final String dummyPdfContent = "";
        final String dummyFile = "some/Random.file";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        FileRspDto fileRspDto = FileRspDto.builder().filePath(dummyFile).build();
        businessData.put(AippConst.BS_PDF_PATH_KEY, JsonUtils.parseObject(JsonUtils.toJsonString(fileRspDto)));

        when(operatorServiceMock.fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.PDF)))).thenReturn(dummyPdfContent);

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(flowData));
        verify(operatorServiceMock, times(1)).fileExtractor(any(), any());
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }
}
