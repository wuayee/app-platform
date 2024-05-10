/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.persist.mapper;

import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import com.huawei.fit.jober.flowsengine.persist.entity.FlowStreamInfo;
import com.huawei.fit.jober.flowsengine.persist.po.FlowDefinitionPO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link FlowDefinitionMapper} 对应测试类
 *
 * @author y00679285
 * @since 2023/7/27
 */
public class FlowDefinitionMapperTest extends DatabaseBaseTest {
    private final String sqlFile = "handler/flowDefinition/saveData.sql";

    private final String user = "admin";

    private FlowDefinitionMapper flowDefinitionMapper;

    @BeforeEach
    void before() {
        flowDefinitionMapper = sqlSessionManager.openSession(true).getMapper(FlowDefinitionMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowDefinition/cleanData.sql");
    }

    private FlowDefinitionPO getFlowDefinitionPO() {
        return FlowDefinitionPO.builder()
                .definitionId("a05f2e169b0e466795886dd5fcf0bb05")
                .metaId("0001")
                .name("流水线创建")
                .version("1.1.1")
                .status(FlowDefinitionStatus.ACTIVE.getCode())
                .tenant("public")
                .createdBy("yxy")
                .graph("{\n" + "    \"name\": \"创建联调分支流程\",\n" + "    \"description\": \"创建联调分支流程\",\n"
                        + "    \"metaId\": \"nyrsc7\",\n" + "    \"version\": \"4\",\n" + "    \"nodes\": []\n" + "}")
                .build();
    }

    @Nested
    @DisplayName("测试流程定义对象数据库功能")
    class TestFlowsDefinitionMapper {
        @Test
        @DisplayName("测试保存流程定义并查询成功")
        public void givenRightParamsThenSaveSuccessfully() {
            FlowDefinitionPO flowDefinitionPO = getFlowDefinitionPO();

            flowDefinitionMapper.create(flowDefinitionPO, LocalDateTime.now());
            FlowDefinitionPO actual = flowDefinitionMapper.find(flowDefinitionPO.getDefinitionId());

            Assertions.assertEquals(actual.getName(), flowDefinitionPO.getName());
        }

        @Test
        @DisplayName("测试删除流程定义成功")
        public void deleteFlowsDefinitionSuccess() {
            executeSqlInFile(sqlFile);
            String definitionId = "0001";

            flowDefinitionMapper.delete(definitionId);
            FlowDefinitionPO actual = flowDefinitionMapper.find(definitionId);

            Assertions.assertNull(actual);
        }

        @Test
        @DisplayName("测试根据租户id查询流程定义成功")
        public void findFlowsByTenantIdSuccess() {
            executeSqlInFile(sqlFile);
            String tenantId = "tianzhou";

            List<FlowDefinitionPO> flowDefinitions = flowDefinitionMapper.findByTenantId(tenantId);

            Assertions.assertEquals(2, flowDefinitions.size());
        }

        @Test
        @DisplayName("测试根据租户id查询流程定义成功")
        public void findFlowsByFlowNameSuccess() {
            executeSqlInFile(sqlFile);
            String name = "流水线创建";
            String version = "0.0.1";

            FlowDefinitionPO flowDefinition = flowDefinitionMapper.findByFlowNameAndVersion(name, version);

            Assertions.assertEquals(flowDefinition.getDefinitionId(), "a05f2e169b0e466795886dd5fcf0bb05");
        }

        @Test
        @DisplayName("测试根据streamId查询流程定义成功")
        public void findFlowsByStreamIdSuccess() {
            executeSqlInFile(sqlFile);
            String metaId = "0001";
            String version = "0.0.1";

            FlowDefinitionPO flowDefinition = flowDefinitionMapper.findByMetaIdAndVersion(metaId, version);

            Assertions.assertEquals(flowDefinition.getDefinitionId(), "a05f2e169b0e466795886dd5fcf0bb05");
        }

        @Test
        @DisplayName("测试根据streamIdList查询流程定义成功")
        public void findFlowsByStreamIdListSuccess() {
            executeSqlInFile(sqlFile);
            List<FlowStreamInfo> streams = new ArrayList<>();
            streams.add(new FlowStreamInfo("0001", "0.0.1"));
            streams.add(new FlowStreamInfo("0002", "0.0.1"));

            List<FlowDefinitionPO> flowDefinition = flowDefinitionMapper.findByStreamIdList(streams);

            Assertions.assertEquals(flowDefinition.size(), 2);
        }
    }
}
