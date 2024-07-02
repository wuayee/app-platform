/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AippInstanceCreateDto;
import com.huawei.fit.jober.aipp.dto.AippInstanceDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.fitable.agent.AippFlowAgent;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AippFlowAgentTest {
    private static final String DUMMY_W3ACCOUNT = "z00000001";
    private static final String DUMMY_TENANT_ID = "tenant_id";
    private static final String DUMMY_AIPP_ID = "main_aipp_id";
    private static final String DUMMY_INST_ID = "main_inst_id";
    private static final String DUMMY_AGENT_INST_ID = "agent_inst_id";
    private static final String DUMMY_RECOMMEND_INFO = "recommend_info";
    private static final String DUMMY_MIND_INFO = "mind_info";
    private static final String DUMMY_INST_URL_LINK = "inst_url_key";
    private static final String DUMMY_ENDPOINT = "test_endpoint";
    private static final String DUMMY_MIND_AGENT_AIPP_ID = "mind_agent";
    private static final String DUMMY_SEARCH_AGENT_AIPP_ID = "search_agent";
    private static final String DUMMY_PROMPT = "prompt";

    @Mock
    private AippRunTimeService aippRunTimeServiceMock;

    @Mock
    private MetaInstanceService metaInstanceServiceMock;

    @Mock
    private AippLogService aippLogServiceMock;

    private AppFlowAgentSearch appFlowAgentSearch;
    private AppFlowAgentMind appFlowAgentMind;
    private AippFlowAgent agent;

    @BeforeEach
    void setUp() {
        agent = new AippFlowAgent(aippRunTimeServiceMock, metaInstanceServiceMock, aippLogServiceMock, DUMMY_ENDPOINT);
        appFlowAgentSearch = new AppFlowAgentSearch(agent, DUMMY_SEARCH_AGENT_AIPP_ID, aippLogServiceMock);
        appFlowAgentMind = new AppFlowAgentMind(agent, DUMMY_MIND_AGENT_AIPP_ID);
    }

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        OperationContext context = new OperationContext();
        context.setTenantId(DUMMY_TENANT_ID);
        context.setW3Account(DUMMY_W3ACCOUNT);
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
        businessData.put(AippConst.BS_AIPP_ID_KEY, DUMMY_AIPP_ID);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, DUMMY_INST_ID);
        businessData.put(AippConst.BS_MODEL_PROMPT_KEY, DUMMY_PROMPT);
        return businessData;
    }

    @Test
    void shouldOkWhenStartSearchAgent() {
        AippInstanceCreateDto dto = AippInstanceCreateDto.builder().instanceId(DUMMY_AGENT_INST_ID).build();
        doReturn(dto).when(aippRunTimeServiceMock).createAippInstanceLatest(any(), any(), any());
        doAnswer(new Answer<Object>() {
            private int times = 0;

            public Object answer(InvocationOnMock invocation) {
                if (++times == 1) {
                    // 触发第一次
                    return AippInstanceDto.builder().status(MetaInstStatusEnum.RUNNING.name()).build();
                }
                // 触发第二次
                return AippInstanceDto.builder()
                        .status(MetaInstStatusEnum.ARCHIVED.name())
                        .formArgs(Collections.singletonMap(AippConst.INST_RECOMMEND_DOC_KEY, DUMMY_RECOMMEND_INFO))
                        .build();
            }
        }).when(aippRunTimeServiceMock).getInstanceByVersionId(any(), any(), any());

        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowDataIn = TestUtils.buildFlowDataWithExtraConfig(businessData, "dorado");
        List<Map<String, Object>> flowData = appFlowAgentSearch.handleTask(flowDataIn);

        Map<String, Object> agentParams = DataUtils.getAgentParams(flowData);
        Assertions.assertTrue(agentParams.containsKey(AippConst.INST_RECOMMEND_DOC_KEY));
    }

    @Test
    void shouldThrowWhenSearchAgentInstEraseError() {
        AippInstanceCreateDto dto = AippInstanceCreateDto.builder().instanceId(DUMMY_AGENT_INST_ID).build();
        doReturn(dto).when(aippRunTimeServiceMock).createAippInstanceLatest(any(), any(), any());
        doReturn(AippInstanceDto.builder().status(MetaInstStatusEnum.ERROR.name()).build()).when(aippRunTimeServiceMock)
                .getInstanceByVersionId(any(), any(), any());

        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, "dorado");
        Assertions.assertThrows(JobberException.class, () -> appFlowAgentSearch.handleTask(flowData));
    }

    @Test
    @Disabled
    void shouldOkWhenStartMindAgent() {
        AippInstanceCreateDto dto = AippInstanceCreateDto.builder().instanceId(DUMMY_AGENT_INST_ID).build();
        doReturn(dto).when(aippRunTimeServiceMock).createAippInstanceLatest(any(), any(), any());
        doReturn(AippInstanceDto.builder()
                .status(MetaInstStatusEnum.ARCHIVED.name())
                .formArgs(Collections.singletonMap(AippConst.INST_MIND_DATA_KEY, DUMMY_MIND_INFO))
                .build()).when(aippRunTimeServiceMock).getInstanceByVersionId(any(), any(), any());

        doAnswer(var -> {
            InstanceDeclarationInfo info = var.getArgument(2);
            String expectedUrl = String.format(agent.getInstUrlFormat(),
                    DUMMY_ENDPOINT,
                    DUMMY_TENANT_ID,
                    DUMMY_MIND_AGENT_AIPP_ID,
                    DUMMY_AGENT_INST_ID);
            Assertions.assertEquals(info.getInfo().getValue().get(AippConst.INST_MIND_URL_KEY), expectedUrl);
            return null;
        }).when(metaInstanceServiceMock).patchMetaInstance(any(), any(), any(), any());

        Map<String, Object> businessData = genBusinessData();
        businessData.put(AippConst.INST_RECOMMEND_DOC_KEY, "recomend");
        List<Map<String, Object>> inputFlowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        List<Map<String, Object>> flowData = appFlowAgentMind.handleTask(inputFlowData);

        Map<String, Object> agentParams = DataUtils.getAgentParams(flowData);
        Assertions.assertTrue(agentParams.containsKey(AippConst.INST_MIND_DATA_KEY));
    }

    @Test
    void shouldThrowWhenMindAgentInstEraseError() {
        String dummyError = "some error msg";
        AippInstLog dummyErrorLog = new AippInstLog();
        dummyErrorLog.setLogType(AippInstLogType.ERROR.name());
        dummyErrorLog.setLogData(dummyError);

        // todo: add version
        doReturn(AippInstanceCreateDto.builder().instanceId(DUMMY_AGENT_INST_ID).build()).when(aippRunTimeServiceMock)
                .createAippInstanceLatest(any(), any(), any());
        doReturn(AippInstanceDto.builder()
                .aippInstanceLogs(Collections.singletonList(dummyErrorLog))
                .status(MetaInstStatusEnum.ERROR.name())
                .build()).when(aippRunTimeServiceMock).getInstanceByVersionId(any(), any(), any());
        Map<String, Object> businessData = genBusinessData();
        businessData.put(AippConst.INST_RECOMMEND_DOC_KEY, "recomend");
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        when(aippLogServiceMock.queryInstanceLogSince(eq(DUMMY_AGENT_INST_ID),
                any())).thenReturn(Collections.singletonList(dummyErrorLog));

        Assertions.assertThrows(JobberException.class, () -> appFlowAgentMind.handleTask(flowData));
        verify(aippLogServiceMock).insertLog(argThat(logDataDto -> logDataDto.getAippId().equals(DUMMY_AIPP_ID)
                && logDataDto.getInstanceId().equals(DUMMY_INST_ID) && logDataDto.getLogType()
                .equals(AippInstLogType.ERROR.name()) && logDataDto.getCreateUserAccount().equals(DUMMY_W3ACCOUNT)));
    }

    @Test
    void shouldThrowWhenStartMindAgentWithInvalidInput() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> appFlowAgentMind.handleTask(Collections.singletonList(Collections.singletonMap(AippConst.BS_DATA_KEY,
                        genBusinessData()))));
    }
}
