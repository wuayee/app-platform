/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import static modelengine.fit.jade.waterflow.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.ARCHIVED;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.ERROR;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jade.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoRepo;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.definitions.FlowDefinition;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.utils.WaterFlows;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 从json文件中获取流程定义的基础类
 *
 * @author 高诗意
 * @since 1.0
 */
public abstract class FlowsDataBaseTest {
    /**
     * 错误节点1
     */
    protected static final String STATE_ERROR_1 = "state-error1";

    /**
     * 错误节点2
     */
    protected static final String STATE_ERROR_2 = "state-error2";

    /**
     * 错误条件节点1
     */
    protected static final String CONDITION_ERROR_1 = "condition-error1";

    /**
     * 线程数量
     */
    protected static final int THREAD_NUM = 2;

    @AfterAll
    static void cleanPublisher() {
        WaterFlows.clear();
    }

    /**
     * 构造一个通用的等待context的表达式
     *
     * @param repo repo
     * @param streamId streamId
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier
     */
    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String streamId,
            String traceId, String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(context -> context.getStreamId().equals(streamId))
                    .filter(context -> context.getPosition().equals(metaId))
                    .filter(context -> context.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * 构造一个通用的等待context的表达式
     *
     * @param repo repo
     * @param traceId traceId
     * @param metaId metaId
     * @param status status
     * @return Supplier
     */
    protected Supplier<List<FlowContext<FlowData>>> contextSupplier(FlowContextRepo<FlowData> repo, String traceId,
            String metaId, FlowNodeStatus status) {
        return () -> {
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(repo, traceId);
            return all.stream()
                    .filter(context -> context.getPosition().equals(metaId))
                    .filter(context -> context.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * 根据trace查询
     *
     * @param repo repo
     * @param traceId traceId
     * @return FlowContext list
     */
    protected List<FlowContext<FlowData>> getContextsByTraceWrapper(FlowContextRepo<FlowData> repo, String traceId) {
        if (repo instanceof FlowContextMemoRepo) {
            return repo.getContextsByTrace(traceId);
        }
        return repo.getContextsByTrace(traceId)
                .stream()
                .map(context -> context.convertData(FlowData.parseFromJson(ObjectUtils.cast(context.getData())),
                        context.getId()))
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
     * 构造一个线程列表，执行指定任务，异常时增加失败次数统计
     *
     * @param latch 结束通知
     * @param failTimes 失败次数
     * @param runnable 任务
     * @return 线程列表
     */
    protected List<Thread> getThreads(CountDownLatch latch, AtomicInteger failTimes, Runnable runnable) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREAD_NUM; i++) {
            Thread thread = new Thread(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
            thread.setUncaughtExceptionHandler((thread1, throwable) -> {
                failTimes.getAndIncrement();
                fail("Fail executor in thread! fail message: " + throwable);
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

    /**
     * 构造flowData
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
        businessData.put("xxxUser", "user");
        businessData.put("xxxBranch", "branch");
        businessData.put("xxxStatus", "status");
        businessData.put("xxxTag", "tag");
        businessData.put("xxxId", "id");
        businessData.put("application", "testApp");
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
        businessData.put("application", "testApp");
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
        businessData.put("application", "testApp");
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
        businessData.put("application", "testApp");
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
        businessData.put("application", "testApp");
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
        businessData.put("application", "testApp");
        return businessData;
    }

    /**
     * 判定单个实例
     *
     * @param existInstance existInstance
     * @param newInstance newInstance
     */
    protected void assertSingleInstance(Publisher<FlowData> existInstance, Publisher<FlowData> newInstance) {
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
        assertEquals("hello: echo: user", resultBusinessData.get("xxxUser"));
        assertEquals("hello: echo: branch", resultBusinessData.get("xxxBranch"));
        assertEquals("echo: status", resultBusinessData.get("xxxStatus"));
        assertEquals("hello: echo: tag", resultBusinessData.get("xxxTag"));
        assertEquals("echo: id", resultBusinessData.get("xxxId"));
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
        assertTrue(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved")));
        assertTrue(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "committer.approved")));
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
        assertFalse(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved")));
        assertTrue(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "committer.approved")));
        assertEquals("success", resultBusinessData.get("approvedResult"));
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
        assertTrue(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "cmc.approved")));
        assertFalse(ObjectUtils.<Boolean>cast(getBusinessDataFromChainedKey(resultBusinessData, "committer.approved")));
        assertEquals("state1: success", resultBusinessData.get("approvedResult"));
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
                .filter(context -> !context.getPosition().equals(metaId))
                .forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals("success", resultBusinessData.get("approvedResult"));
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
                .filter(context -> !context.getPosition().equals(metaId))
                .forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
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
        resumeAllContexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
        Map<String, Object> resumeResultBusinessData = resumeContexts.get(0).getData().getBusinessData();
        assertEquals(approvedExpected, resumeResultBusinessData.get("approvedResult"));
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
        allContexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));

        Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
        assertEquals(6, resultBusinessData.size());
        assertEquals("branch", resultBusinessData.get("xxxBranch"));
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
        contexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
        contexts.forEach(
                context -> assertEquals("hello: success", context.getData().getBusinessData().get("approvedResult")));
        assertEquals(12, allContexts.size());
        allContexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
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
        contexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
        assertEquals(14, allContexts.size());
        allContexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
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
        all.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
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
        contexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
        assertEquals(10, allContexts.size());
        allContexts.forEach(context -> assertEquals(ARCHIVED, context.getStatus()));
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
        assertTrue(contexts.get(0).getData().getErrorMessage().startsWith(
                errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName())));
        assertEquals(expectedNodeNumber, allContexts.size());
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
        assertTrue(contexts.get(0).getData().getErrorMessage().startsWith(
                errorMessage(flowDefinition.getStreamId(), metaId, flowNode.getName())));
        assertEquals(3, allContexts.size());
    }

    /**
     * 断言jober调用错误
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
                metaId, flowNode.getName(), WaterflowException.class.getSimpleName(), errorMsg);
        String actual = contexts.get(0).getData().getErrorMessage();
        assertEquals(MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), flowDefinition.getStreamId(), metaId,
                        flowNode.getName(), WaterflowException.class.getSimpleName(), errorMsg),
                contexts.get(0).getData().getErrorMessage());
        assertEquals(2, allContexts.size());
        verify(invoker, times(1)).invoke(any(), anyList(), anyString());
    }

    /**
     * 构造错误
     *
     * @param streamId streamId
     * @param metaId metaId
     * @param name name
     * @return String
     */
    protected String errorMessage(String streamId, String metaId, String name) {
        String errorMsg = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, metaId, name,
                WaterflowException.class.getSimpleName(), String.format(Locale.ROOT,
                        "execute jober failed, jober name: %s, jober type: ECHO_JOBER, fitables: [], errors:", name));
        return errorMsg.substring(0, errorMsg.length() - 1);
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
