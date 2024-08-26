/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowRetryPO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link FlowRetryMapper} 对应测试类
 *
 * @author 李哲峰
 * @since 2024/1/31
 */
public class FlowRetryMapperTest extends DatabaseBaseTest {
    private final String sqlFile = "handler/flowRetry/saveData.sql";

    private FlowRetryMapper flowRetryMapper;

    @BeforeEach
    void before() {
        flowRetryMapper = sqlSessionManager.openSession(true).getMapper(FlowRetryMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowRetry/cleanData.sql");
    }

    @Test
    @DisplayName("测试flow retry创建并查询成功")
    public void testFlowRetryBatchCreateSuccess() {
        FlowRetryPO flowRetryPO = getFlowRetryPO("batchId", "toBatch", 1);
        flowRetryMapper.batchCreate(Collections.singletonList(flowRetryPO));
        FlowRetryPO result = flowRetryMapper.find(flowRetryPO.getEntityId());
        assertEquals(flowRetryPO.getNextRetryTime(), result.getNextRetryTime());
        assertEquals(flowRetryPO.getNextRetryTime(), result.getLastRetryTime());
        assertEquals(flowRetryPO.getRetryCount(), result.getRetryCount());
        assertEquals(flowRetryPO.getVersion(), result.getVersion());
    }

    @Test
    @DisplayName("测试flow retry更新重试记录成功")
    public void testFlowRetryBatchUpdateRetryRecordSuccess() {
        executeSqlInFile(sqlFile);
        FlowRetryPO flowRetryPO = getFlowRetryPO("1", "context", 1);
        assertEquals(1, flowRetryMapper.batchUpdateRetryRecord(Collections.singletonList(flowRetryPO)));
        FlowRetryPO result = flowRetryMapper.find("1");
        assertEquals(flowRetryPO.getNextRetryTime(), result.getNextRetryTime());
        assertEquals(flowRetryPO.getLastRetryTime(), result.getLastRetryTime());
        assertEquals(flowRetryPO.getRetryCount(), result.getRetryCount());
        assertEquals(2, result.getVersion());
    }

    @Test
    @DisplayName("测试flow retry更新重试记录失败")
    public void testFlowRetryBatchUpdateRetryRecordFailure() {
        executeSqlInFile(sqlFile);
        FlowRetryPO flowRetryPO = getFlowRetryPO("1", "context", 2);
        assertEquals(0, flowRetryMapper.batchUpdateRetryRecord(Collections.singletonList(flowRetryPO)));
        FlowRetryPO result = flowRetryMapper.find("1");
        assertNotEquals(flowRetryPO.getNextRetryTime(), result.getNextRetryTime());
        assertNotEquals(flowRetryPO.getLastRetryTime(), result.getLastRetryTime());
        assertNotEquals(flowRetryPO.getRetryCount(), result.getRetryCount());
        assertEquals(1, result.getVersion());
    }

    @Test
    @DisplayName("测试flow retry更新下次重试时间成功")
    public void testFlowRetryBatchUpdateNextRetryTimeSuccess() {
        executeSqlInFile(sqlFile);
        LocalDateTime nextRetryTime = LocalDateTime.now();
        flowRetryMapper.batchUpdateNextRetryTime(Collections.singletonList("1"), nextRetryTime);
        FlowRetryPO result = flowRetryMapper.find("1");
        assertEquals(nextRetryTime, result.getNextRetryTime());
        assertEquals(2, result.getVersion());
    }

    @Test
    @DisplayName("测试flow retry依据时间筛选重试记录成功")
    public void testFlowRetryFilterNextRetryTimeSuccess() {
        executeSqlInFile(sqlFile);
        List<String> retryList = new ArrayList<>();
        List<FlowRetryPO> flowRetryPOList = flowRetryMapper.filterByNextRetryTime(LocalDateTime.now(), retryList);
        assertNotNull(flowRetryPOList);
        assertEquals(2, flowRetryPOList.size());
        assertEquals("1", flowRetryPOList.get(0).getEntityId());
        assertEquals("2", flowRetryPOList.get(1).getEntityId());
    }

    @Test
    @DisplayName("测试flow retry删除成功")
    public void testFlowRetryBatchDeleteSuccess() {
        executeSqlInFile(sqlFile);
        assertNotNull(flowRetryMapper.find("1"));
        flowRetryMapper.batchDelete(Collections.singletonList("1"));
        assertNull(flowRetryMapper.find("1"));
    }

    private FlowRetryPO getFlowRetryPO(String entityId, String entityType, int version) {
        LocalDateTime time = LocalDateTime.parse("2024-02-01T16:30:01.011");
        return FlowRetryPO.builder()
                .entityId(entityId)
                .entityType(entityType)
                .nextRetryTime(time)
                .lastRetryTime(time)
                .retryCount(10)
                .version(version)
                .build();
    }
}
