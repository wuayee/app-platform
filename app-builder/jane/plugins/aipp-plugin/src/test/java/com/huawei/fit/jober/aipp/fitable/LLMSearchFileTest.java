/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class LLMSearchFileTest {
    private static final String DUMMY_ID = "id";
    private static final String DUMMY_PROMPT = "prompt_data";

    @InjectMocks
    private LLMSearchFile llmSearchFile;

    @Mock
    private LLMService llmServiceMock;

    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(new OperationContext()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, DUMMY_ID);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, DUMMY_ID);
        businessData.put(AippConst.BS_MODEL_NAME_KEY, LlmModelNameEnum.XIAOHAI.getValue());
        businessData.put(AippConst.BS_AGENT_RESULT_LINK_KEY, AippConst.INST_RECOMMEND_DOC_KEY);
        businessData.put(AippConst.BS_MODEL_PROMPT_KEY, "dorado");
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, DUMMY_ID);
        return businessData;
    }

    @Test
    void shouldOkWhenSearchFile() throws IOException {
        Map<String, Object> businessData = genBusinessData();

        FileDto dto = new FileDto();
        dto.setFileType("pdf");
        dto.setFileName("name");
        dto.setFileUrl("url");
        doAnswer(var -> Collections.singletonList(dto)).when(llmServiceMock).askXiaoHaiFile(any(), any());

        doAnswer(var -> {
            InstanceDeclarationInfo info = var.getArgument(2);
            Assertions.assertTrue(info.getInfo().getDefined());
            Assertions.assertTrue(info.getInfo().getValue().containsKey(AippConst.INST_RECOMMEND_DOC_KEY));
            Assertions.assertEquals(info.getInfo().getValue().get(AippConst.INST_RECOMMEND_DOC_KEY),
                    JsonUtils.toJsonString(Collections.singletonList(dto)));
            return null;
        }).when(metaInstanceServiceMock).patchMetaInstance(any(), any(), any(), any());

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, DUMMY_PROMPT);
        List<Map<String, Object>> list = llmSearchFile.handleTask(flowData);
        Assertions.assertNotNull(list);
    }

    @Test
    @Disabled
    void shouldThrowWhenProvideUnSupportModel() {
        Map<String, Object> businessData = genBusinessData();
        businessData.put(AippConst.BS_MODEL_NAME_KEY, LlmModelNameEnum.QWEN_14B.getValue());

        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, DUMMY_PROMPT);
        Assertions.assertThrows(JobberException.class, () -> llmSearchFile.handleTask(flowData));
    }

    @Test
    void shouldThrowWhenProvideInvalidFlowData() {
        Assertions.assertThrows(JobberException.class, () -> llmSearchFile.handleTask(Collections.emptyList()));

        Assertions.assertThrows(JobberException.class,
                () -> llmSearchFile.handleTask(Collections.singletonList(Collections.singletonMap("invalid key",
                        genBusinessData()))));
    }
}
