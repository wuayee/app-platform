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
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.UUIDUtil;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CompositeFileReaderTest {
    private final String dummyId = "someRandomId";
    private final String dummyContent = "some random Content";
    @Mock
    private OperatorService operatorServiceMock;
    @Mock
    private MetaInstanceService metaInstanceServiceMock;
    private AippLogService aippLogServiceMock;
    private CompositeFileReader fileReader;

    @BeforeEach
    void setUp() {
        this.fileReader = new CompositeFileReader(operatorServiceMock, metaInstanceServiceMock, aippLogServiceMock);
    }

    @Test
    void shouldCallOperatorServiceWhenCallHandleTaskWithPdf() {
        // given
        final String dummyFile = "some/Random.pdf";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_FILE_PATH_KEY,
                JsonUtils.parseObject(String.format("{\"file_path\":\"%s\"}", dummyFile)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        when(operatorServiceMock.fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.PDF)))).thenReturn(dummyContent);
        // when
        List<Map<String, Object>> flowData = fileReader.handleTask(Collections.singletonList(Collections.singletonMap(
                AippConst.BS_DATA_KEY,
                businessData)));
        // then
        verify(operatorServiceMock, times(1)).fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.PDF)));
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_FILE2TEXT_KEY).equals(dummyContent)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
        Assertions.assertEquals(Utils.getBusiness(flowData).get(AippConst.INST_FILE2TEXT_KEY), dummyContent);
    }

    @Test
    void shouldCallOperatorServiceWhenCallHandleTaskWithDocx() {
        // given
        final String dummyFile = "some/Random.docx";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_FILE_PATH_KEY,
                JsonUtils.parseObject(String.format("{\"file_path\":\"%s\"}", dummyFile)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        when(operatorServiceMock.fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.WORD)))).thenReturn(dummyContent);
        // when
        List<Map<String, Object>> flowData = fileReader.handleTask(Collections.singletonList(Collections.singletonMap(
                AippConst.BS_DATA_KEY,
                businessData)));
        // then
        verify(operatorServiceMock, times(1)).fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.WORD)));
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(info -> info.getInfo().getValue().get(AippConst.INST_FILE2TEXT_KEY).equals(dummyContent)),
                argThat(OperationContextDummy::operationContextDummyMatcher));
        Assertions.assertEquals(Utils.getBusiness(flowData).get(AippConst.INST_FILE2TEXT_KEY), dummyContent);
    }

    @Test
    void shouldThrowWhenCallHandleTaskWithOperatorServiceReturnBlank() {
        // given
        final String dummyFile = "some/Random.docx";
        final String dummyPath = Paths.get(Utils.NAS_SHARE_DIR, dummyFile).toAbsolutePath().toString();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_FILE_PATH_KEY,
                JsonUtils.parseObject(String.format("{\"file_path\":\"%s\"}", dummyFile)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        when(operatorServiceMock.fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.WORD)))).thenReturn("");
        // when
        Assertions.assertThrows(JobberException.class,
                () -> fileReader.handleTask(Collections.singletonList(Collections.singletonMap(AippConst.BS_DATA_KEY,
                        businessData))));
        // then
        verify(operatorServiceMock, times(1)).fileExtractor(argThat(file -> file.getAbsolutePath().equals(dummyPath)),
            java.util.Optional.ofNullable(eq(OperatorService.FileType.WORD)));
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }

    @Test
    void shouldThrowWhenCallHandleTaskWithNonExistTextFile() {
        // given
        final String nonExistFile = UUIDUtil.uuid() + ".txt";
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_FILE_PATH_KEY,
                JsonUtils.parseObject(String.format("{\"file_path\":\"%s\"}", nonExistFile)));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);
        // when
        Assertions.assertThrows(JobberException.class,
                () -> fileReader.handleTask(Collections.singletonList(Collections.singletonMap(AippConst.BS_DATA_KEY,
                        businessData))));
        // then
        verify(operatorServiceMock, never()).fileExtractor(any(), any());
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }
}
