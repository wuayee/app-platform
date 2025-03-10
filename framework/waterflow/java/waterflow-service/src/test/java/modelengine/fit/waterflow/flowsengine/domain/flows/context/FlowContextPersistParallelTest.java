/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import static modelengine.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.MAX_WAIT_TIME_MS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * flow context持久化测试
 *
 * @author 杨祥宇
 * @since 2023/8/23
 */
@ExtendWith(MethodNameLoggerExtension.class)
@Disabled("设计不合理，并发的子流程并行时会有mock的冲突问题")
@DisplayName("流程实例在数据库中并行运行测试集合")
public class FlowContextPersistParallelTest extends DatabaseBaseTest {
    @Nested
    @DisplayName("流程实例流转多并发场景测试集合，复用数据库持久化测试场景")
    class FlowAutoParallelExecutorInPersist extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        private final FlowContextPersistTest flowContextPersistTest = new FlowContextPersistTest();

        private final FlowContextPersistTest.FlowAutoExecutorInPersistTest flowAutoExecutorInPersistTest
                = flowContextPersistTest.new FlowAutoExecutorInPersistTest();

        private final FlowContextPersistTest.FlowManualExecutorInPersistTest flowManualExecutorInPersistTest
                = flowContextPersistTest.new FlowManualExecutorInPersistTest();

        private final FlowContextPersistTest.FlowAutoExecutorInPersistWithJoberIncludeFitableTest
                flowAutoExecutorInPersistWithJoberIncludeFitableTest
                = flowContextPersistTest.new FlowAutoExecutorInPersistWithJoberIncludeFitableTest();

        private final FlowContextPersistTest.FlowAutoExecutorInPersistMToNTest flowAutoExecutorInPersistMToNTest
                = flowContextPersistTest.new FlowAutoExecutorInPersistMToNTest();

        private final FlowContextPersistTest.FlowAutoExecutorInPersistMToNWithFilterBatchSizeOne
                flowAutoExecutorInPersistMToNWithFilterMinimumSizeOne
                = flowContextPersistTest.new FlowAutoExecutorInPersistMToNWithFilterBatchSizeOne();

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点的并发场景")
        void testFlowsExecutorWithOnlyStateNode1To1Parallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInPersistTest::testFlowsExecutorWithOnlyStateNode1To1);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第一个节点错误的并发场景")
        void testFlowsExecutorStateNodeWithErrorForFirstNode1To1Parallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInPersistTest::testFlowsExecutorStateNodeWithErrorForFirstNode1To1);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过并发场景")
        void testFlowsExecutorWithConditionNodeFirstBranchTrueParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInPersistTest::testFlowsExecutorWithConditionNodeFirstBranchTrue);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支2驳回并发场景")
        void testFlowsExecutorWithConditionNodeSecondBranchFalseParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInPersistTest::testFlowsExecutorWithConditionNodeSecondBranchFalse);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点异常并发场景")
        void testFlowsExecutorConditionNodeWithErrorParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInPersistTest::testFlowsExecutorConditionNodeWithError);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1通过的并发场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchTrueParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowManualExecutorInPersistTest::testFlowsManualExecutorWithConditionNodeFirstBranchTrue);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1驳回的并发场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchFalseParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowManualExecutorInPersistTest::testFlowsManualExecutorWithConditionNodeFirstBranchFalse);
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务并发场景测试")
        void testFlowsExecuteGeneralJoberParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                flowAutoExecutorInPersistWithJoberIncludeFitableTest.testFlowsExecuteGeneralJober();
            });
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer一个数据的并发场景测试")
        void testFlowsExecuteProduceFromMToNForOfferOneDataParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                flowAutoExecutorInPersistMToNTest.testFlowsExecuteProduceFromMToNForOfferOneData();
            });
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例m到n最小SIZE过滤器为多单线程offer的场景测试")
        void testFlowsExecuteProduceFromMToNWithMinimumSizeOneInMultiThread() throws Throwable {
            CountDownLatch latch = new CountDownLatch(threadNum);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                flowAutoExecutorInPersistMToNWithFilterMinimumSizeOne
                        .testFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread();
            });
            threads.forEach(Thread::start);
            assertTrue(latch.await(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS));
            assertEquals(0, failTimes.get());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }
}
