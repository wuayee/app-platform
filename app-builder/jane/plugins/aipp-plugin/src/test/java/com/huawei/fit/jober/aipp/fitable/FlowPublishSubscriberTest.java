/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import com.huawei.fit.jober.aipp.enums.AppState;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fit.jober.entity.FlowPublishContext;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * publishSubscriber测试用例.
 *
 * @author 张越
 * @since 2024-07-29
 */
@ExtendWith(MockitoExtension.class)
public class FlowPublishSubscriberTest {
    @Mock
    private MetaService metaService;

    @Mock
    private AppBuilderAppFactory appFactory;

    @Mock
    private AppBuilderRuntimeInfoRepository repository;

    private FlowPublishSubscriber flowPublishSubscriber;

    /**
     * 初始化.
     */
    @BeforeEach
    void setUp() {
        this.flowPublishSubscriber = new FlowPublishSubscriber(this.metaService, this.appFactory, this.repository);
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldAttributesMatchWhenOnPublish() {
        // before
        FlowPublishContext context = new FlowPublishContext();
        context.setTraceId("trace1");
        context.setStatus(FlowNodeStatus.ARCHIVED.name());
        context.setCreateAt(LocalDateTime.now());
        context.setArchivedAt(LocalDateTime.now());

        FlowNodePublishInfo publishInfo = new FlowNodePublishInfo();
        publishInfo.setNodeId("node1");
        publishInfo.setNodeType(NodeTypes.START.getType());
        publishInfo.setFlowDefinitionId("flow1");
        publishInfo.setErrorMsg("error1");
        publishInfo.setFlowContext(context);
        publishInfo.setBusinessData(this.buildBusinessData());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        doReturn(metaRangedResultSet).when(this.metaService)
                .list(any(MetaFilter.class), anyBoolean(), anyLong(), anyInt(), any(OperationContext.class),
                        any(MetaFilter.class));

        AppBuilderApp appBuilderApp = new AppBuilderApp(null, null, null, null, null);
        appBuilderApp.setState(AppState.PUBLISHED.getName());
        doReturn(appBuilderApp).when(this.appFactory).create(anyString());

        AtomicReference<AppBuilderRuntimeInfo> reference = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            AppBuilderRuntimeInfo message = ObjectUtils.cast(args[0]);
            reference.set(message);
            return null;
        }).when(this.repository).insertOne(any(AppBuilderRuntimeInfo.class));

        // when
        this.flowPublishSubscriber.publishNodeInfo(publishInfo);

        // then
        AppBuilderRuntimeInfo info = reference.get();
        assertEquals(info.getTraceId(), "trace1");
        assertEquals(info.getNodeId(), "node1");
        assertEquals(info.getFlowDefinitionId(), "flow1");
        assertEquals(info.getInstanceId(), "instance1");
        assertEquals(info.getNodeType(), NodeTypes.START.getType());
        assertEquals(info.getStatus(), FlowNodeStatus.ARCHIVED.name());
        assertTrue(info.isPublished());
        assertEquals(info.getParameters().size(), 1);
        assertEquals(info.getParameters().get(0).getInput(), "1");
        assertEquals(info.getParameters().get(0).getOutput(), "2");
    }

    private Map<String, Object> buildBusinessData() {
        Map<String, Object> params = new HashMap<>();
        params.put("input", 1);
        params.put("output", 2);

        List<Map<String, Object>> nodeInfo = new ArrayList<>();
        nodeInfo.add(params);

        Map<String, Object> executeInfo = new HashMap<>();
        executeInfo.put("node1", nodeInfo);

        Map<String, Object> internal = new HashMap<>();
        internal.put("executeInfo", executeInfo);

        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, "instance1");
        businessData.put(AippConst.BS_AIPP_ID_KEY, "app1");
        businessData.put(AippConst.BS_AIPP_VERSION_KEY, "version1");

        OperationContext context = new OperationContext();
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
        businessData.put("_internal", internal);
        return businessData;
    }
}
