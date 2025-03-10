/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.common.RangedResultSet;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TestUtils {
    /**
     * 测试用的对话实例id
     */
    public static final String DUMMY_FLOW_INSTANCE_ID = "testInstanceId";

    /**
     * 测试用的子对话实例id
     */
    public static final String DUMMY_CHILD_INSTANCE_ID = "testChildInstanceId";

    private static final String DUMMY_FLOW_CONFIG_ID = "testFlowConfigId";

    private static final String DUMMY_FLOW_DEF_ID = "testFlowDefId";

    private static final String DUMMY_FLOW_CONFIG_VERSION = "1.0.0";

    private static final List<String> TEST_TRACE_IDS = Collections.singletonList("testTraceId");

    private static final String APP_ID = "appId1";

    public static List<Map<String, Object>> buildFlowDataWithExtraConfig(Map<String, Object> businessData,
            String dummyPrompt) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(AippConst.BS_DATA_KEY, businessData);

        Map<String, Object> extraJober = Collections.singletonMap(AippConst.BS_MODEL_PROMPT_KEY, dummyPrompt);
        flowData.put(AippConst.CONTEXT_DATA_KEY, new HashMap<String, Object>() {{
            put(AippConst.BS_EXTRA_CONFIG_KEY, extraJober);
            put(AippConst.INST_FLOW_TRACE_IDS, TEST_TRACE_IDS);
            put("contextId", "testContextId");
        }});
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
        Meta expectMeta = new Meta();
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
        attribute.put(AippConst.ATTR_APP_ID_KEY, APP_ID);
        expectMeta.setAttributes(attribute);
        expectMeta.setProperties(Collections.emptyList());
        return expectMeta;
    }

    public static void mockMetaListReturnSingleItem5(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock)
                .list(any(), eq(true), eq(0L), eq(1), any());
    }

    public static CountDownLatch mockResumeFlow(FlowInstanceService flowInstanceServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).resumeAsyncJob(any(), any(), any());
        return countDownLatch;
    }

    public static void mockMetaListReturnSingleItem6(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock)
                .list(any(), eq(true), eq(0L), eq(1), any(), any());
    }

    public static CountDownLatch mockFailAsyncJob(FlowInstanceService flowInstanceServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).failAsyncJob(any(), any(), any());
        return countDownLatch;
    }
}
