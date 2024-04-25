/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.ERROR;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.utils.WaterFlows;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 从json文件中获取流程定义的基础类
 *
 * @author g00564732
 * @since 1.0
 */
public abstract class FlowsDataBaseTest {
    protected final String stateError1 = "state-error1";

    protected final String stateError2 = "state-error2";

    protected final String conditionError1 = "condition-error1";

    protected final int threadNum = 2;

    @AfterAll
    static void cleanPublisher() {
        WaterFlows.clear();
    }

    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String streamId,
            String traceId, String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(c -> c.getStreamId().equals(streamId))
                    .filter(c -> c.getPosition().equals(metaId))
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String traceId,
            String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(c -> c.getPosition().equals(metaId))
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    protected List<FlowContext<FlowData>> getContextsByTraceWrapper(FlowContextRepo<FlowData> repo, String traceId) {
        if (repo instanceof FlowContextMemoRepo) {
            return repo.getContextsByTrace(traceId);
        }
        return repo.getContextsByTrace(traceId)
                .stream()
                .map(c -> c.convertData(FlowData.parseFromJson(ObjectUtils.cast(c.getData())), c.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取流程定义解析的json数据
     *
     * @param fileName 流程定义的json文件
     * @return 流程定义的json字符串
     */
    protected String getJsonData(String fileName) {
        try (InputStream in = IoUtils.resource(FlowsDataBaseTest.class.getClassLoader(), fileName)) {
            return new String(IoUtils.read(in), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    protected List<Thread> getThreads(CountDownLatch latch, AtomicInteger failTimes, Runnable runnable) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    failTimes.getAndIncrement();
                    fail("Fail executor in thread! fail message: " + e);
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
        }
        return threads;
    }

    /**
     * 获取文件路径
     *
     * @param fileName 流程定义的json文件名
     * @return 流程定义的json文件路径
     */
    protected String getFilePath(String fileName) {
        return getFilePathPrefix() + fileName;
    }

    /**
     * 获取文件路径前缀
     *
     * @return 文件路径前缀
     */
    protected abstract String getFilePathPrefix();

    protected FlowData getFlowData(Map<String, Object> businessData, String operator) {
        return FlowData.builder()
                .operator(operator)
                .startTime(LocalDateTime.now())
                .businessData(businessData)
                .contextData(new HashMap<>())
                .build();
    }

    protected Map<String, Object> flowsExecutorWithOnlyStateNode1To1() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cudehub.user", "user");
        businessData.put("cudehub.branch", "branch");
        businessData.put("libing.status", "status");
        businessData.put("cudehub.tag", "tag");
        businessData.put("libing.id", "id");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected Map<String, Object> flowsExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cmc.approved", "true");
        businessData.put("committer.approved", "true");
        businessData.put("approved.result", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected Map<String, Object> flowsManualExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected Map<String, Object> flowsManualExecutorWithConditionNodeCircle() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected Map<String, Object> flowsExecuteProduceFromMToNForOfferOneData(String approved) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("approved", approved);
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected Map<String, Object> flowsExecuteFilterFromMToN() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    protected String errorMessage(String streamId, String metaId, String name) {
        return MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                WaterflowException.class.getSimpleName(), String.format(
                        "execute jober failed, jober name: %s, jober type: ECHO_JOBER, fitables: [], errors: null",
                        name));
    }

    protected void assertSingleInstance(Publisher<FlowData> existInstance, Publisher<FlowData> newInstance) {
        assertEquals(System.identityHashCode(existInstance), System.identityHashCode(newInstance));
    }

    /**
     * 断言是否全部完成
     *
     * @param flowData 提供businessData的数量判定
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecutorWithOnlyStateNode1To1(FlowData flowData, List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(4, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(flowData.getBusinessData().size(), resultBusinessData.size());
        assertEquals("hello: echo: user", resultBusinessData.get("cudehub.user"));
        assertEquals("hello: echo: branch", resultBusinessData.get("cudehub.branch"));
        assertEquals("echo: status", resultBusinessData.get("libing.status"));
        assertEquals("hello: echo: tag", resultBusinessData.get("cudehub.tag"));
        assertEquals("echo: id", resultBusinessData.get("libing.id"));
    }

    /**
     * 断言是否全部完成
     *
     * @param flowDefinition 定义
     * @param metaId 节点id
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     * @param expectedNodeNumber 期望的节点数量
     */
    protected void assertFlowsExecutorStateNodeWithError(FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, int expectedNodeNumber) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(expectedNodeNumber, allContexts.size());
    }

    /**
     * 断言是否全部完成
     *
     * @param flowData 提供businessData的数量判定
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstBranchTrue(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(6, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(resultBusinessData.size(), flowData.getBusinessData().size());
        assertTrue(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("cmc.approved"))));
        assertTrue(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("committer.approved"))));
        assertEquals("state2: state1: success", resultBusinessData.get("approved.result"));
    }

    /**
     * 断言是否全部完成
     *
     * @param flowData 提供businessData的数量判定
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(resultBusinessData.size(), flowData.getBusinessData().size());
        assertFalse(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("cmc.approved"))));
        assertTrue(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("committer.approved"))));
        assertEquals("success", resultBusinessData.get("approved.result"));
    }

    /**
     * 断言是否全部完成
     *
     * @param flowData 提供businessData的数量判定
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecutorWithConditionNodeSecondFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(5, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(resultBusinessData.size(), flowData.getBusinessData().size());
        assertTrue(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("cmc.approved"))));
        assertFalse(Boolean.parseBoolean(ObjectUtils.cast(resultBusinessData.get("committer.approved"))));
        assertEquals("state1: success", resultBusinessData.get("approved.result"));
    }

    /**
     * 断言是否全部完成
     *
     * @param flowDefinition 定义
     * @param metaId 流程的唯一id
     * @param flowNode 节点
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecutorConditionNodeWithError(FlowDefinition flowDefinition, String metaId,
            FlowNode flowNode, List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(3, allContexts.size());
    }

    /**
     * 断言是否全部完成
     *
     * @param metaId 流程的唯一id
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(PENDING, contexts.get(0).getStatus());
        assertEquals(2, allContexts.size());
        allContexts.stream()
                .filter(c -> !c.getPosition().equals(metaId))
                .forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals("success", resultBusinessData.get("approved.result"));
    }

    /**
     * 断言是否全部完成
     *
     * @param metaId 节点id
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     * @param allSize 期望的总数量
     * @param status 期望的节点状态
     */
    protected void assertFlowsManualExecutorWithConditionNodeCircle(String metaId, List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts, int allSize, FlowNodeStatus status) {
        assertEquals(1, contexts.size());
        assertEquals(status, contexts.get(0).getStatus());
        assertEquals(allSize, allContexts.size());
        allContexts.stream()
                .filter(c -> !c.getPosition().equals(metaId))
                .forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * 断言是否全部完成，且数量正确
     *
     * @param resumeContexts 当前的contexts
     * @param resumeAllContexts 过程中产生的contexts
     * @param approvedExpected 期望的审批通过数量
     * @param contextSizeExpected 期望的context数量
     */
    protected void assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(
            List<FlowContext<FlowData>> resumeContexts, List<FlowContext<FlowData>> resumeAllContexts,
            int contextSizeExpected, String approvedExpected) {
        assertEquals(1, resumeContexts.size());
        assertEquals(contextSizeExpected, resumeAllContexts.size());
        resumeAllContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resumeResultBusinessData = resumeContexts.get(0).getData().getBusinessData();
        assertEquals(approvedExpected, resumeResultBusinessData.get("approved.result"));
    }

    /**
     * 断言是否全部完成
     *
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecuteGeneralJober(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(6, resultBusinessData.size());
        assertEquals("branch", resultBusinessData.get("cudehub.branch"));
    }

    protected void assertFlowsExecuteGeneralJoberError(Invoker invoker, FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        String errorMsg = "execute jober failed, jober name: 通知, jober type: GENERAL_JOBER, fitables: [创建分支实现], "
                + "errors: execute jober failed, jober name: {0}, jober type: {1}, fitables: {2}, errors: {3}";
        String expected = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), flowDefinition.getStreamId(),
                metaId, flowNode.getName(), WaterflowException.class.getSimpleName(), errorMsg);
        String actual = contexts.get(0).getData().getErrorMessage();
        assertEquals(MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), flowDefinition.getStreamId(), metaId,
                        flowNode.getName(), WaterflowException.class.getSimpleName(), errorMsg),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(2, allContexts.size());
        verify(invoker, times(1)).invoke(any(), anyList(), anyString());
    }

    /**
     * 断言是否全部完成
     *
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread(
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(5, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        contexts.forEach(c -> assertEquals("hello: success", c.getData().getBusinessData().get("approved.result")));
        assertEquals(12, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * 断言是否全部完成
     *
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferMultiData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(14, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * 断言contexts成功
     *
     * @param repo context的repo
     * @param traceId traceId
     * @param endContexts 结束的context列表
     */
    protected void assertFlowsExecuteFilterFromMToN(FlowContextRepo<FlowData> repo, String traceId,
            List<FlowContext<FlowData>> endContexts) {
        List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
        assertEquals(3, endContexts.size());
        assertEquals(10, all.size());
        all.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * 断言是否全部完成
     *
     * @param contexts 当前的contexts
     * @param allContexts 过程中产生的contexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferOneData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(10, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }
}
