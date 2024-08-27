/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.jade.app.engine.metrics.service.ConversationRecordService;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link AippFlowEndCallback} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-22
 */
@FitTestWithJunit(includeClasses = {AippFlowEndCallback.class})
class AippFlowEndCallbackTest {
    @Mock
    private MetaService metaService;
    @Mock
    private AippLogService aippLogService;
    @Mock
    private ConversationRecordService conversationRecordService;
    @Mock
    private AppBuilderFormService formService;
    @Mock
    private MetaInstanceService metaInstanceService;
    @Mock
    private AppChatSseService appChatSseService;
    @Fit
    private AippFlowEndCallback aippFlowEndCallback;

    private static Map<String, Object> buildBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("tools", Collections.emptyList());
        businessData.put("workflows", Collections.emptyList());
        businessData.put("model", "test_model");
        businessData.put("temperature", 0.7);
        businessData.put("systemPrompt", "");
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(new OperationContext()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, "LlmComponentTest");
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, "tool_data");
        businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, "version");
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());
        return businessData;
    }


    @Test
    void test_callback_should_void_when_test_data_combination() {
        AppBuilderFormPropertyRepository formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        AppBuilderForm appBuilderForm = new AppBuilderForm(formPropertyRepository);
        when(this.formService.selectWithId(anyString())).thenReturn(appBuilderForm);
        when(this.metaService.retrieve(anyString(), any(OperationContext.class))).thenReturn(TestUtils.buildMeta());

        this.aippFlowEndCallback.callback(TestUtils.buildFlowDataWithExtraConfig(buildBusinessData(), null));

        verify(conversationRecordService).insertConversationRecord(any());
    }
}