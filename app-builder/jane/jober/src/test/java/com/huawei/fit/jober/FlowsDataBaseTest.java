/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ERROR;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import com.huawei.fit.waterflow.flowsengine.utils.WaterFlows;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.log.Logger;
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
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 从json文件中获取流程定义的基础类
 *
 * @author 高诗意
 * @since 2023/08/22
 */
public abstract class FlowsDataBaseTest {
    private static final Logger log = Logger.get(FlowsDataBaseTest.class);

    /**
     * 失败节点1
     */
    protected final String stateError1 = "state-error1";

    /**
     * 失败节点2
     */
    protected final String stateError2 = "state-error2";

    /**
     * 失败条件1
     */
    protected final String conditionError1 = "condition-error1";

    /**
     * 线程数
     */
    protected final int threadNum = 2;

    @AfterAll
    static void cleanPublisher() {
        WaterFlows.clear();
    }

    /**
     * contextSupplier
     *
     * @param repo repo
     * @param streamId streamId
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier<List < FlowContext < FlowData>>>
     */
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

    /**
     * contextSupplier
     *
     * @param repo repo
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier<List < FlowContext < FlowData>>>
     */
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

    /**
     * getContextsByTraceWrapper
     *
     * @param repo repo
     * @param traceId traceId
     * @return List<FlowContext < FlowData>>
     */
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

    /**
     * getThreads
     *
     * @param latch latch
     * @param failTimes failTimes
     * @param runnable runnable
     * @return Thread集合
     */
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
            thread.setUncaughtExceptionHandler((tr, ex) -> log.error(tr.getName() + " : " + ex.getMessage()));
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

    /**
     * getFlowData
     *
     * @param businessData businessData
     * @param operator operator
     * @return FlowData
     */
    protected FlowData getFlowData(Map<String, Object> businessData, String operator) {
        return FlowData.builder()
                .operator(operator)
                .startTime(LocalDateTime.now())
                .businessData(businessData)
                .contextData(new HashMap<>())
                .build();
    }

    /**
     * flowsExecutorWithOnlyStateNode1To1
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecutorWithOnlyStateNode1To1() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cudehubUser", "user");
        businessData.put("cudehubBranch", "branch");
        businessData.put("libingStatus", "status");
        businessData.put("cudehubTag", "tag");
        businessData.put("libingId", "id");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * flowsExecutorWithConditionNodeFirstBranchTrue
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("cmc", new HashMap<String, Boolean>() {
            {
                put("approved", true);
            }
        });
        businessData.put("committer", new HashMap<String, Boolean>() {
            {
                put("approved", true);
            }
        });
        businessData.put("approvedResult", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * flowsManualExecutorWithConditionNodeFirstBranchTrue
     *
     * @return businessData
     */
    protected Map<String, Object> flowsManualExecutorWithConditionNodeFirstBranchTrue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * flowsManualExecutorWithConditionNodeCircle
     *
     * @return businessData
     */
    protected Map<String, Object> flowsManualExecutorWithConditionNodeCircle() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approved.result", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * flowsExecuteProduceFromMToNForOfferOneData
     *
     * @param approved approved
     * @return businessData
     */
    protected Map<String, Object> flowsExecuteProduceFromMToNForOfferOneData(boolean approved) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("approved", approved);
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * flowsExecuteFilterFromMToN
     *
     * @return businessData
     */
    protected Map<String, Object> flowsExecuteFilterFromMToN() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("approvedResult", "success");
        businessData.put("application", "tianzhou");
        return businessData;
    }

    /**
     * errorMessage
     *
     * @param streamId streamId
     * @param metaId metaId
     * @param name name
     * @return 错误信息
     */
    protected String errorMessage(String streamId, String metaId, String name) {
        return MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                JobberException.class.getSimpleName(), String.format(
                        "execute jober failed, jober name: %s, jober type: ECHO_JOBER, fitables: [], errors: null",
                        name));
    }

    protected String errorMessage(String streamId, String metaId, String name, Exception exception) {
        return MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                exception.getClass().getSimpleName(),
                Optional.ofNullable(exception.getMessage()).orElse("internal error"));
    }

    /**
     * assertSingleInstance
     *
     * @param existInstance existInstance
     * @param newInstance newInstance
     */
    protected void assertSingleInstance(
            FitStream.Publisher<FlowData> existInstance, FitStream.Publisher<FlowData> newInstance) {
        assertEquals(System.identityHashCode(existInstance), System.identityHashCode(newInstance));
    }

    /**
     * assertFlowsExecutorWithOnlyStateNode1To1
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithOnlyStateNode1To1(FlowData flowData, List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(4, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals("hello: echo: user", resultBusinessData.get("cudehubUser"));
        assertEquals("hello: echo: branch", resultBusinessData.get("cudehubBranch"));
        assertEquals("echo: status", resultBusinessData.get("libingStatus"));
        assertEquals("hello: echo: tag", resultBusinessData.get("cudehubTag"));
        assertEquals("echo: id", resultBusinessData.get("libingId"));
    }

    /**
     * assertFlowsExecutorStateNodeWithError
     *
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     * @param expectedNodeNumber expectedNodeNumber
     */
    protected void assertFlowsExecutorStateNodeWithError(FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, int expectedNodeNumber) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()).substring(0, 20),
                contexts.get(0).getData().getErrorMessage().substring(0, 20));
        assertEquals(expectedNodeNumber, allContexts.size());
    }

    protected void assertFlowsExecutorStateNodeWithException(FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts, int expectedNodeNumber,
            Exception exception) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName(), exception),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(expectedNodeNumber, allContexts.size());
    }

    /**
     * assertFlowsExecutorWithConditionNodeFirstBranchTrue
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstBranchTrue(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(6, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("state2: state1: success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorWithConditionNodeFirstFalseBranch
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeFirstFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertFalse((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorWithConditionNodeSecondFalseBranch
     *
     * @param flowData flowData
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorWithConditionNodeSecondFalseBranch(FlowData flowData,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(5, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertTrue((boolean) getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved"));
        assertFalse((boolean) getBusinessDataFromChainedKey(resultBusinessData, "committer.approved"));
        assertEquals("state1: success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecutorConditionNodeWithError
     *
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param flowNode flowNode
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecutorConditionNodeWithError(FlowDefinition flowDefinition, String metaId,
            FlowNode flowNode, List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        assertEquals(errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName()).substring(0, 20),
                contexts.get(0).getData().getErrorMessage().substring(0, 20));
        assertEquals(3, allContexts.size());
    }

    /**
     * assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock
     *
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
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
        assertEquals("success", resultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsManualExecutorWithConditionNodeCircle
     *
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     * @param allSize allSize
     * @param status status
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
     * assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock
     *
     * @param resumeContexts resumeContexts
     * @param resumeAllContexts resumeAllContexts
     * @param contextSizeExpected contextSizeExpected
     * @param approvedExpected approvedExpected
     */
    protected void assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(
            List<FlowContext<FlowData>> resumeContexts, List<FlowContext<FlowData>> resumeAllContexts,
            int contextSizeExpected, String approvedExpected) {
        assertEquals(1, resumeContexts.size());
        assertEquals(contextSizeExpected, resumeAllContexts.size());
        resumeAllContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        Map<String, Object> resumeResultBusinessData = resumeContexts.get(0).getData().getBusinessData();
        assertEquals(approvedExpected, resumeResultBusinessData.get("approvedResult"));
    }

    /**
     * assertFlowsExecuteGeneralJober
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteGeneralJober(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(1, contexts.size());
        assertEquals(ARCHIVED, contexts.get(0).getStatus());
        assertEquals(3, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(6, resultBusinessData.size());
        assertEquals("branch", resultBusinessData.get("cudehubBranch"));
    }

    /**
     * assertFlowsExecuteGeneralJoberError
     *
     * @param invoker invoker
     * @param flowDefinition flowDefinition
     * @param metaId metaId
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteGeneralJoberError(Invoker invoker, FlowDefinition flowDefinition, String metaId,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        FlowNode flowNode = flowDefinition.getFlowNode(metaId);
        assertEquals(1, contexts.size());
        assertEquals(ERROR, contexts.get(0).getStatus());
        String errorMsg = "execute jober failed, jober name: 通知, jober type: GENERAL_JOBER, fitables: [创建分支实现], "
                + "errors: execute jober failed, jober name: {0}, jober type: {1}, fitables: {2}, errors: {3}";
        String expected = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), flowDefinition.getStreamId(),
                metaId, flowNode.getName(), JobberException.class.getSimpleName(), errorMsg);
        String actual = contexts.get(0).getData().getErrorMessage();
        assertEquals(expected, actual);
        assertEquals(2, allContexts.size());
        verify(invoker, times(1)).invoke(any(), anyList(), anyString());
    }

    /**
     * assertFlowsExecuteGeneralJoberWithRetryableException
     *
     * @param from from
     * @param cxtPosition cxtPosition
     * @param contexts contexts
     * @param allContexts allContexts
     * @param retryMapper retryMapper
     */
    protected void assertFlowsExecuteGeneralJoberWithRetryableException(From<FlowData> from, String cxtPosition,
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts,
            FlowRetryMapper retryMapper) {
        assertEquals(1, contexts.size());
        assertEquals(RETRYABLE, contexts.get(0).getStatus());
        assertEquals(cxtPosition, contexts.get(0).getPosition());
        assertEquals(2, allContexts.size());
        FlowRetryPO retryPO = retryMapper.find(contexts.get(0).getToBatch());
        assertNotNull(retryPO.getNextRetryTime());
        assertNull(retryPO.getLastRetryTime());
        assertEquals(retryPO.getRetryCount(), 0);
        FitStream.Subscriber<FlowData, Object> subscriberWithException = from.getSubscriber(
                contexts.get(0).getPosition());
        assertTrue(subscriberWithException.isAuto());
        assertEquals(cxtPosition, subscriberWithException.getId());
    }

    /**
     * assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread(
            List<FlowContext<FlowData>> contexts, List<FlowContext<FlowData>> allContexts) {
        assertEquals(5, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        contexts.forEach(c -> assertEquals("hello: success", c.getData().getBusinessData().get("approvedResult")));
        assertEquals(12, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteProduceFromMToNForOfferMultiData
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferMultiData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(14, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteFilterFromMToN
     *
     * @param repo repo
     * @param traceId traceId
     * @param endContexts endContexts
     */
    protected void assertFlowsExecuteFilterFromMToN(FlowContextRepo<FlowData> repo, String traceId,
            List<FlowContext<FlowData>> endContexts) {
        List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
        assertEquals(3, endContexts.size());
        assertEquals(10, all.size());
        all.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    /**
     * assertFlowsExecuteProduceFromMToNForOfferOneData
     *
     * @param contexts contexts
     * @param allContexts allContexts
     */
    protected void assertFlowsExecuteProduceFromMToNForOfferOneData(List<FlowContext<FlowData>> contexts,
            List<FlowContext<FlowData>> allContexts) {
        assertEquals(3, contexts.size());
        contexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
        assertEquals(10, allContexts.size());
        allContexts.forEach(c -> assertEquals(ARCHIVED, c.getStatus()));
    }

    private Object getBusinessDataFromChainedKey(Map<String, Object> businessData, String keyChain) {
        Map<String, Object> businessDataCopy = businessData;
        String[] keys = keyChain.split("\\.");
        Object result = "";
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                result = businessDataCopy.get(keys[i]);
                break;
            }
            if (!(businessDataCopy.get(keys[i]) instanceof Map)) {
                throw new IllegalStateException(
                        String.format("Failed to get the value of \"%s\" from the businessData %s", keyChain,
                                businessData));
            }
            businessDataCopy = ObjectUtils.cast(businessDataCopy.get(keys[i]));
        }
        return result;
    }
}
