/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.AippMetaStatusEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.common.RangeResult;
import com.huawei.fit.jober.common.RangedResultSet;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TestUtils {
    private static final String DUMMY_FLOW_CONFIG_ID = "testFlowConfigId";
    private static final String DUMMY_FLOW_DEF_ID = "testFlowDefId";
    public static final String DUMMY_FLOW_INSTANCE_ID = "testInstanceId";
    private static final String DUMMY_FLOW_CONFIG_VERSION = "1.0.0";

    public static List<Map<String, Object>> buildFlowDataWithExtraConfig(Map<String, Object> businessData,
            String dummyPrompt) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(AippConst.BS_DATA_KEY, businessData);

        Map<String, Object> extraJober = Collections.singletonMap(AippConst.BS_MODEL_PROMPT_KEY, dummyPrompt);
        flowData.put(AippConst.CONTEXT_DATA_KEY, Collections.singletonMap(AippConst.BS_EXTRA_CONFIG_KEY, extraJober));
        return Collections.singletonList(flowData);
    }

    public static List<Map<String, Object>> buildFlowDataWithExtraConfig(Map<String, Object> businessData,
            String dummyPrompt, boolean logEnable) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(AippConst.BS_DATA_KEY, businessData);
        Map<String, Object> extraJober = new HashMap<String, Object>() {
            {
                put(AippConst.BS_MODEL_PROMPT_KEY, dummyPrompt);
                put(AippConst.BS_LOG_ENABLE_KEY, String.valueOf(logEnable));
            }
        };

        flowData.put(AippConst.CONTEXT_DATA_KEY, Collections.singletonMap(AippConst.BS_EXTRA_CONFIG_KEY, extraJober));
        return Collections.singletonList(flowData);
    }

    public static Meta buildMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        com.huawei.fit.jane.meta.multiversion.definition.Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId("testId");
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);

        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attribute.put(AippConst.ATTR_VERSION_KEY, DUMMY_FLOW_CONFIG_VERSION);
        attribute.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        attribute.put(AippConst.ATTR_FLOW_DEF_ID_KEY, DUMMY_FLOW_DEF_ID);
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    public static void mockMetaListReturnSingleItem5(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock)
                .list(any(), eq(true), eq(0L), eq(1), any());
    }

    public static void mockReturnFlowTraceId(MetaInstanceService instanceServiceMock) {
        Instance instance = new Instance();
        instance.setInfo(new HashMap<String, String>() {{
            put(AippConst.INST_FLOW_INST_ID_KEY, DUMMY_FLOW_INSTANCE_ID);
        }});
        RangedResultSet<Instance> resultSet =
                new RangedResultSet<>(Collections.singletonList(instance), new RangeResult(0, 1, 1));
        Mockito.when(instanceServiceMock.list(any(), any(), anyLong(), anyInt(), any())).thenReturn(resultSet);
    }

    public static CountDownLatch mockResumeFlow(FlowInstanceService flowInstanceServiceMock,
            MetaService metaServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.when(metaServiceMock.retrieve(any(), any())).thenReturn(buildMeta());
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).resumeAsyncJob(any(), any(), any(), any());
        return countDownLatch;
    }

    public static void mockMetaListReturnSingleItem6(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock).list(any(), eq(true), eq(0L), eq(1), any(), any());
    }

    public static CountDownLatch mockTerminateFlow(FlowInstanceService flowInstanceServiceMock,
            MetaService metaServiceMock, AippLogService aippLogServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.when(metaServiceMock.retrieve(any(), any())).thenReturn(buildMeta());
        Mockito.doAnswer((Answer<Void>) invocation -> null).when(aippLogServiceMock).insertLog(any());
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).terminateFlows(any(), any(), any(), any());
        return countDownLatch;
    }
}
