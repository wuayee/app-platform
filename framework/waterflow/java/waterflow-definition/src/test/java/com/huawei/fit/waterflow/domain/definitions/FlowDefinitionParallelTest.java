/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions;

import com.huawei.fit.waterflow.FlowsDataBaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流程定义核心测试类
 *
 * @author 高诗意
 * @since 1.0
 */
@Disabled
@DisplayName("流程实例在内存中并行运行测试集合")
class FlowDefinitionParallelTest {
    @Nested
    @DisplayName("流程实例流转多并发场景测试集合，复用内存持久化测试场景")
    class FlowAutoParallelExecutorInPersist extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        private final FlowDefinitionTest flowDefinitionTest = new FlowDefinitionTest();

        private final FlowDefinitionTest.FlowAutoExecutorInMemoryTest flowAutoExecutorInMemoryTest =
                flowDefinitionTest.new FlowAutoExecutorInMemoryTest();

        private final FlowDefinitionTest.FlowManualExecutorInMemoryTest flowManualExecutorInMemoryTest =
                flowDefinitionTest.new FlowManualExecutorInMemoryTest();

        private final FlowDefinitionTest.FlowAutoExecutorInMemoryWithJoberIncludeFitableTest
                flowAutoExecutorInMemoryWithJoberIncludeFitableTest =
                flowDefinitionTest.new FlowAutoExecutorInMemoryWithJoberIncludeFitableTest();

        private final FlowDefinitionTest.FlowAutoExecutorInMemoryMtoNtest flowAutoExecutorInMemoryMToNTest =
                flowDefinitionTest.new FlowAutoExecutorInMemoryMtoNtest();

        private final FlowDefinitionTest.FlowAutoExecutorInPersistMtoNwithFilterMinimumSizeOne
                flowAutoExecutorInPersistMToNWithFilterMinimumSizeOne =
                flowDefinitionTest.new FlowAutoExecutorInPersistMtoNwithFilterMinimumSizeOne();

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点的并发场景")
        void testFlowsExecutorWithOnlyStateNode1To1Parallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInMemoryTest::testFlowsExecutorWithOnlyStateNode1To1);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第一个节点错误的并发场景")
        void testFlowsExecutorStateNodeWithErrorForFirstNode1To1Parallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInMemoryTest::testFlowsExecutorStateNodeWithErrorForFirstNode1To1);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过并发场景")
        void testFlowsExecutorWithConditionNodeFirstBranchTrueParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInMemoryTest::testFlowsExecutorWithConditionNodeFirstBranchTrue);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支2驳回并发场景")
        void testFlowsExecutorWithConditionNodeSecondBranchFalseParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInMemoryTest::testFlowsExecutorWithConditionNodeSecondBranchFalse);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点异常并发场景")
        void testFlowsExecutorConditionNodeWithErrorParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowAutoExecutorInMemoryTest::testFlowsExecutorConditionNodeWithError);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1通过的并发场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchTrueParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowManualExecutorInMemoryTest::testFlowsManualExecutorWithConditionNodeFirstBranchTrue);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1驳回的并发场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchFalseParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes,
                    flowManualExecutorInMemoryTest::testFlowsManualExecutorWithConditionNodeFirstBranchFalse);
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务并发场景测试")
        void testFlowsExecuteGeneralJoberParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                try {
                    flowAutoExecutorInMemoryWithJoberIncludeFitableTest.testFlowsExecuteGeneralJober();
                } catch (Throwable e) {
                    failTimes.getAndIncrement();
                    Assertions.fail("Fail executor in thread! fail message: " + e);
                }
            });
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer一个数据的并发场景测试")
        void testFlowsExecuteProduceFromMToNForOfferOneDataParallel() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                try {
                    flowAutoExecutorInMemoryMToNTest.testFlowsExecuteProduceFromMToNForOfferOneData();
                } catch (Throwable e) {
                    failTimes.getAndIncrement();
                    Assertions.fail("Fail executor in thread! fail message: " + e);
                }
            });
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Test
        @DisplayName("流程实例m到n最小SIZE过滤器为多单线程offer的场景测试")
        void testFlowsExecuteProduceFromMToNWithMinimumSizeOneInMultiThread() throws Throwable {
            CountDownLatch latch = new CountDownLatch(FlowsDataBaseTest.THREAD_NUM);
            AtomicInteger failTimes = new AtomicInteger(0);
            List<Thread> threads = getThreads(latch, failTimes, () -> {
                try {
                    flowAutoExecutorInPersistMToNWithFilterMinimumSizeOne
                            .testFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread();
                } catch (Throwable e) {
                    failTimes.getAndIncrement();
                    Assertions.fail("Fail executor in thread! fail message: " + e);
                }
            });
            threads.forEach(Thread::start);
            latch.await();
            Assertions.assertEquals(0, failTimes.get());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }
}
