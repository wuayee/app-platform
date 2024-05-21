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

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileListDto;
import com.huawei.fit.jober.aipp.dummy.OperationContextDummy;
import com.huawei.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LLMJson2MindTest {
    private LLMJson2Mind llmFitable;
    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    private static String getXiaoHaiAnswerString() {
        return "{\"res\": [{\"fileName\": \"pdf1.pdf\", \"fileUrl\": \"pdf1_url\", \"fileType\": \"pdf\"}, "
                + "{\"fileName\": \"pdf2.pdf\", \"fileUrl\": \"pdf2_url\", \"fileType\": \"pdf\"}, {\"fileName\": "
                + "\"word1.docx\", \"fileUrl\": \"word1_url\", \"fileType\": \"docx\"}, {\"fileName\": \"word2"
                + ".docx\", \"fileUrl\": \"word2_url\", \"fileType\": \"docx\"}, {\"fileName\": \"ppt1.ppt\", "
                + "\"fileUrl\": \"ppt1_url\", \"fileType\": \"ppt\"}, {\"fileName\": \"ppt2.ppt\", \"fileUrl\": "
                + "\"ppt2_url\", \"fileType\": \"ppt\"}]}";
    }

    private static String getMindJsonString() {
        return "{\"name\":\"大模型检索结果\",\"children\":[{\"name\":\"pdf\",\"children\":[{\"name\":\"pdf1.pdf\","
                + "\"children\":[{\"name\":\"cGRmMV91cmw=\",\"children\":[]}]},{\"name\":\"pdf2.pdf\","
                + "\"children\":[{\"name\":\"cGRmMl91cmw=\",\"children\":[]}]}]},{\"name\":\"ppt\","
                + "\"children\":[{\"name\":\"ppt1.ppt\",\"children\":[{\"name\":\"cHB0MV91cmw=\",\"children\":[]}]},"
                + "{\"name\":\"ppt2.ppt\",\"children\":[{\"name\":\"cHB0Ml91cmw=\",\"children\":[]}]}]},"
                + "{\"name\":\"docx\",\"children\":[{\"name\":\"word1.docx\","
                + "\"children\":[{\"name\":\"d29yZDFfdXJs\",\"children\":[]}]},{\"name\":\"word2.docx\","
                + "\"children\":[{\"name\":\"d29yZDJfdXJs\",\"children\":[]}]}]}]}";
    }

    private static List<FileDto> getXiaoHaiAnswer() {
        FileListDto result = JsonUtils.parseObject(getXiaoHaiAnswerString(), FileListDto.class);
        result.getRes().forEach(fileDto -> {
            String url64 = Base64.getEncoder().encodeToString(fileDto.getFileUrl().getBytes(StandardCharsets.UTF_8));
            fileDto.setFileUrl(url64);
        });
        return result.getRes();
    }

    @BeforeEach
    void setUp() {
        this.llmFitable = new LLMJson2Mind(metaInstanceServiceMock);
    }

    @Test
    @Disabled
    void shouldSendExpectRequestWhenCallHandleTask() {
        // given
        final String dummyId = "someRandomId";
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(OperationContextDummy.getDummy()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, dummyId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, dummyId);

        List<Map<String, Object>> flowData =
                TestUtils.buildFlowDataWithExtraConfig(businessData, JsonUtils.toJsonString(getXiaoHaiAnswer()));
        // when
        llmFitable.handleTask(flowData);
        // then
        verify(metaInstanceServiceMock, times(1)).patchMetaInstance(eq(dummyId),
                eq(dummyId),
                argThat(new InstanceDeclarationInfoMatcher()),
                argThat(OperationContextDummy::operationContextDummyMatcher));
    }

    @Test
    void shouldThrowWhenCallHandleTaskWithNoFlowData() {
        Assertions.assertThrows(JobberException.class, () -> llmFitable.handleTask(new ArrayList<>()));
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }

    @Test
    void shouldThrowWhenCallHandleTaskWithNoBusinessDataInFlowData() {
        Assertions.assertThrows(JobberException.class,
                () -> llmFitable.handleTask(Collections.singletonList(Collections.singletonMap(" ", " "))));
        verify(metaInstanceServiceMock, never()).patchMetaInstance(any(), any(), any(), any());
    }

    static class InstanceDeclarationInfoMatcher implements ArgumentMatcher<InstanceDeclarationInfo> {
        private String lastMindJson;

        public InstanceDeclarationInfoMatcher() {
        }

        @Override
        public boolean matches(InstanceDeclarationInfo obj) {
            if (obj.getInfo().getValue().get(AippConst.INST_MIND_DATA_KEY).toString().equals(getMindJsonString())) {
                return true;
            }
            lastMindJson = obj.getInfo().getValue().get(AippConst.INST_MIND_DATA_KEY).toString();
            return false;
        }

        @Override
        public String toString() {
            return "InstanceDeclarationInfoMatcher\n-----------\nExpected: " + getMindJsonString() + "\nGot: "
                    + lastMindJson + "\n----------";
        }
    }
}
