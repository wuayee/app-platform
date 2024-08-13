/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.QueryFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.FlowValidator;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowsService对应测试类
 *
 * @author 杨祥宇
 * @since 2023/9/4
 */
class FlowsServiceTest extends DatabaseBaseTest {
    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowDefinition/cleanData.sql");
    }

    @Nested
    @DisplayName("测试流程定义service")
    class FlowServiceTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/services/";

        private final String sqlPath = "handler/flowDefinition/saveData.sql";

        private final FlowParser parser = new FlowParser(null);

        private final FlowValidator flowValidator = new FlowValidator(new ArrayList<>());

        private DefaultFlowDefinitionRepo flowDefinitionRepo;

        private FlowContextPersistRepo flowContextPersistRepo;

        private QueryFlowDefinitionRepo queryFlowDefinitionRepo;

        private FlowsService flowsService;

        @Mock
        private FitableUsageMapper fitableUsageMapper;

        @BeforeEach
        void setUp() {
            FlowDefinitionMapper flowDefinitionMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowDefinitionMapper.class);
            FlowContextMapper flowContextMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowContextMapper.class);
            FlowTraceMapper flowTraceMapper = sqlSessionManager.openSession(true).getMapper(FlowTraceMapper.class);
            FlowTraceRepo flowTraceRepo = new DefaultFlowTraceRepo(flowTraceMapper);
            FlowRetryMapper flowRetryMapper = sqlSessionManager.openSession(true).getMapper(FlowRetryMapper.class);
            FlowRetryRepo flowRetryRepo = new DefaultFlowRetryRepo(flowRetryMapper);
            flowDefinitionRepo = new DefaultFlowDefinitionRepo(flowDefinitionMapper, fitableUsageMapper, parser);
            Integer defaultLimitation = 5;
            boolean useLimit = false;
            flowContextPersistRepo = new FlowContextPersistRepo(flowContextMapper, flowTraceRepo, flowRetryRepo, null,
                    defaultLimitation, useLimit);
            queryFlowDefinitionRepo = new QueryFlowDefinitionRepo(flowDefinitionMapper);
            flowsService = new FlowsService(parser, flowValidator, flowDefinitionRepo, flowContextPersistRepo,
                    queryFlowDefinitionRepo);
        }

        @Test
        @DisplayName("测试flows service创建流程定义成功")
        @Disabled
        public void testFlowsServiceCreateTestSuccess() {
            String jsonData = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            OperationContext operationContext = getOperationContext();
            FlowDefinition flows = flowsService.createFlows(jsonData, operationContext);

            Assertions.assertEquals("创建流程定义时状态为active", flows.getName());
            Assertions.assertEquals(4, flows.getNodeIdSet().size());
        }

        @Test
        @DisplayName("测试flows service根据租户Id查询流程定义成功")
        public void testFlowServiceFindByTenantTestSuccess() {
            executeSqlInFile(sqlPath);

            List<FlowDefinitionPO> flows = flowsService.findFlowsByTenant(getOperationContext());

            Assertions.assertEquals(2, flows.size());
        }

        @Test
        @DisplayName("测试flows service根据流程名称和版本查询流程定义成功")
        public void testFlowServiceFindByNameVersionTestSuccess() {
            executeSqlInFile(sqlPath);

            FlowDefinitionPO flow = flowsService.findFlowsByName("流水线创建", "0.0.1", getOperationContext());

            Assertions.assertEquals("0001", flow.getMetaId());
        }

        @Test
        @DisplayName("测试flows service更新流程状态成功")
        @Disabled
        public void testFlowServiceUpdateStatusTestSuccess() {
            String jsonData1 = getJsonData(getFilePath("flows_create_with_state_node_and_status_active.json"));
            String jsonData2 = getJsonData(getFilePath("flows_update_with_state_node_and_status_inactive.json"));
            FlowDefinition beforeFlow = flowsService.createFlows(jsonData1, getOperationContext());
            flowsService.updateFlows(beforeFlow.getDefinitionId(), jsonData2, getOperationContext());
            FlowDefinition afterFlow = flowsService.findFlowsById(beforeFlow.getDefinitionId(), getOperationContext());

            Assertions.assertEquals(FlowDefinitionStatus.INACTIVE, afterFlow.getStatus());
        }

        @Test
        @DisplayName("测试flows service根据metaId和version查询流程定义成功")
        public void testFlowServiceQueryFlowTestSuccess() {
            executeSqlInFile(sqlPath);

            FlowDefinitionPO definitionPO = flowsService.findFlowsByMetaIdAndVersion("0001", "0.0.1",
                    getOperationContext());

            Assertions.assertEquals(FlowDefinitionStatus.ACTIVE.getCode(), definitionPO.getStatus());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private OperationContext getOperationContext() {
            return OperationContext.custom().operator("yxy").operatorIp("0.0.0.1").tenantId("tianzhou").build();
        }

        @Test
        @DisplayName("测试自动生成ohScript代码成功")
        public void testGetScript() {
            List<String> fitableIds = new ArrayList<>();
            fitableIds.add("11111111");
            fitableIds.add("222222222");
            String expectedScript = "let context1 = entity{\n" +
                    "    .id = \"11111111\";\n" +
                    ".async = true;\n" +
                    ".format = \"cbor\";\n" +
                    "};\n" +
                    "let f1 = fit::handleTask(context1);\n" +
                    "\n" +
                    "let context2 = entity{\n" +
                    "    .id = \"222222222\";\n" +
                    ".async = true;\n" +
                    ".format = \"cbor\";\n" +
                    "};\n" +
                    "let f2 = fit::handleTask(context2);\n" +
                    "\n" +
                    "ext::context >> f1 >> f2";
            String actualScript = flowsService.getScript(fitableIds);
            Assertions.assertEquals(expectedScript, actualScript);
        }
    }
}