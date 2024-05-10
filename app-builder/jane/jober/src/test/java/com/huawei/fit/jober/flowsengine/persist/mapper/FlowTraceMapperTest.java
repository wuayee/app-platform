/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.persist.mapper;

import static com.huawei.fit.jober.common.Constant.TRACE_EXCLUSIVE_STATUS_MAP;

import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowTraceStatus;
import com.huawei.fit.jober.flowsengine.persist.po.FlowTracePO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link FlowTraceMapper} 测试类
 *
 * @author 00693950
 * @since 2023/12/13
 */
public class FlowTraceMapperTest extends DatabaseBaseTest {
    private static final String TERMINATE_STATUS = FlowTraceStatus.TERMINATE.toString();

    private static final String RUNNING_STATUS = FlowTraceStatus.RUNNING.toString();

    private static final String ARCHIVED_STATUS = FlowTraceStatus.ARCHIVED.toString();

    private static final String ERROR_STATUS = FlowTraceStatus.ERROR.toString();

    private final String sqlFile = "handler/flowTrace/saveData.sql";

    private FlowTraceMapper flowTraceMapper;

    @BeforeEach
    void before() {
        flowTraceMapper = sqlSessionManager.openSession(true).getMapper(FlowTraceMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowTrace/cleanData.sql");
    }

    @Test
    @DisplayName("如果trace的状态是RUNNING，可以更新为TERMINATE")
    public void testUpdateTraceTerminateSuccessWhenStatusIsRunning() {
        executeSqlInFile(sqlFile);

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(TERMINATE_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ARCHIVED，不能更新为TERMINATE")
    public void testUpdateTraceTerminateFailedWhenStatusIsArchived() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ARCHIVED_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(ARCHIVED_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(ARCHIVED_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ERROR，不能更新为TERMINATE")
    public void testUpdateTraceTerminateFailedWhenStatusIsError() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ERROR_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(ERROR_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是TERMINATE，不能更新为RUNNING")
    public void testUpdateTraceRunningFailedWhenStatusIsTerminate() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), TERMINATE_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(TERMINATE_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), RUNNING_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(RUNNING_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(TERMINATE_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("如果trace的状态是ERROR，可以更新为RUNNING")
    public void testUpdateTraceRunningSuccessWhenStatusIsError() {
        executeSqlInFile(sqlFile);
        flowTraceMapper.updateStatus(Collections.singletonList("1"), ERROR_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(ERROR_STATUS));

        flowTraceMapper.updateStatus(Collections.singletonList("1"), RUNNING_STATUS, LocalDateTime.now(),
                TRACE_EXCLUSIVE_STATUS_MAP.get(RUNNING_STATUS));

        FlowTracePO tracePO = flowTraceMapper.find("1");
        Assertions.assertEquals(RUNNING_STATUS, tracePO.getStatus());
    }

    @Test
    @DisplayName("根据traceId列表查询trace成功")
    public void testFindByTraceIdListSuccess() {
        executeSqlInFile(sqlFile);

        List<FlowTracePO> flowTracePOS = flowTraceMapper.findByTraceIdList(Arrays.asList("1", "2"));

        Assertions.assertEquals("1", flowTracePOS.get(0).getTraceId());
        Assertions.assertEquals("RUNNING", flowTracePOS.get(0).getStatus());
        Assertions.assertEquals("2", flowTracePOS.get(1).getTraceId());
        Assertions.assertEquals("ERROR", flowTracePOS.get(1).getStatus());
    }
}
