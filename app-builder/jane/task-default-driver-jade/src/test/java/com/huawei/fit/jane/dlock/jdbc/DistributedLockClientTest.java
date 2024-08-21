/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.dlock.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.huawei.fit.jane.common.utils.SleepUtil;
import com.huawei.fit.jane.dlock.DatabaseBaseTest;
import com.huawei.fit.jane.dlock.jdbc.persist.mapper.FlowLockMapper;
import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import modelengine.fitframework.log.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁核心测试类
 *
 * @author 李哲峰
 * @since 2023/12/01
 */
@DisplayName("分布式锁中核心测试集合")
public class DistributedLockClientTest extends DatabaseBaseTest {
    private static final Logger log = Logger.get(DistributedLockClientTest.class);

    private final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    private DistributedLockRepo repo;

    private DistributedLockClient client;

    @BeforeEach
    void before() {
        repo = new DefaultDistributedLockRepo(sqlSessionManager.openSession(true).getMapper(FlowLockMapper.class));
        client = new DistributedLockClient(10_000, 5_000, repo, mock(InvalidDistributedLockNotify.class));
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowlock/cleanData.sql");
    }

    @Test
    @DisplayName("测试调用lock方法时，客户端的锁池容量大小变化")
    public void testClientLockPoolSize() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock = client.getLock("test");
            lock.lock();
            try {
                assertEquals(1, getLocks().size());
            } finally {
                lock.unlock();
            }
        }
        SleepUtil.sleep(10);
        client.deleteExpiredLocks(0);
        assertEquals(0, getLocks().size());
    }

    @Test
    @DisplayName("测试调用lockInterruptibly方法时，客户端的锁池容量大小变化")
    public void testLockPoolSizeWithLockInterruptibly() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock = client.getLock("test");
            lock.lockInterruptibly();
            try {
                assertEquals(getLocks().size(), 1);
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试同一客户端中，相同key对应的分布式锁是否被复用")
    public void testSameLockForLock() {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lock();
            try {
                Lock lock2 = client.getLock("test");
                assertSame(lock1, lock2);
                lock2.lock();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试调用lockInterruptibly方法时，相同key对应的分布式锁是否被复用")
    public void testSameLockForLockInterruptibly() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lockInterruptibly();
            try {
                Lock lock2 = client.getLock("test");
                assertSame(lock1, lock2);
                lock2.lockInterruptibly();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试key不同的两个锁是否为同一把锁")
    public void testLocksWithDifferentKey() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lockInterruptibly();
            try {
                Lock lock2 = client.getLock("bar");
                assertNotSame(lock1, lock2);
                lock2.lockInterruptibly();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @Disabled
    @DisplayName("测试当线程没有成功获取锁的情况下尝试解锁的场景")
    public void testUnlockFailureWithoutAcquiringLockFirst() throws Exception {
        final Lock lock1 = client.getLock("test");
        lock1.lockInterruptibly();
        final AtomicBoolean locked = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        Future<Object> result = threadExecutor.submit(() -> {
            Lock lock2 = client.getLock("test");
            locked.set(lock2.tryLock(200, TimeUnit.MILLISECONDS));
            latch.countDown();
            try {
                lock2.unlock();
            } catch (Exception e) {
                return e;
            }
            return null;
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertFalse(locked.get());
        lock1.unlock();
        assertTrue(result.get(10, TimeUnit.SECONDS)
                .toString()
                .contains("The current thread doesn't own the lock at lock key test"));
    }

    @Test
    @DisplayName("测试两个线程先后调用lockInterruptibly上锁的场景")
    public void testTwoThreadsLockInterruptibly() throws Exception {
        final Lock lock1 = client.getLock("test");
        final AtomicBoolean locked = new AtomicBoolean();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        lock1.lockInterruptibly();
        threadExecutor.execute(() -> {
            Lock lock2 = client.getLock("test");
            try {
                latch1.countDown();
                lock2.lockInterruptibly();
                latch2.await(10, TimeUnit.SECONDS);
                locked.set(true);
            } catch (InterruptedException ignored) {
                log.error("testTwoThreadsLockInterruptibly error");
            } finally {
                lock2.unlock();
                latch3.countDown();
            }
        });
        assertTrue(latch1.await(10, TimeUnit.SECONDS));
        assertFalse(locked.get());
        lock1.unlock();
        latch2.countDown();
        assertTrue(latch3.await(10, TimeUnit.SECONDS));
        assertTrue(locked.get());
    }

    @Test
    @Disabled
    @DisplayName("测试线程解锁非本线程的锁的场景")
    public void testUnlockForOtherThread() throws Exception {
        final Lock lock = client.getLock("test");
        lock.lockInterruptibly();
        final CountDownLatch latch = new CountDownLatch(1);
        Future<Object> result = threadExecutor.submit(() -> {
            try {
                lock.unlock();
            } catch (Exception e) {
                latch.countDown();
                return e;
            }
            return null;
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        lock.unlock();
        assertTrue(result.get(10, TimeUnit.SECONDS)
                .toString()
                .contains("The current thread doesn't own the lock at lock key test"));
    }

    @Test
    @DisplayName("测试超过客户端锁池容量上限的并发上锁解锁")
    @Disabled("无法运行通过")
    public void testLockPoolCapacityExceeded() throws Exception {
        final int keyCount = 500;
        final int capacityCount = 179;
        final int threadCount = 4;

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        client.setCacheCapacity(capacityCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < keyCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                } catch (InterruptedException ignored) {
                    log.error("testLockPoolCapacityExceeded error");
                }
                String keyId = "test:" + finalI;
                Lock getLock = client.getLock(keyId);
                getLock.lock();
                getLock.unlock();
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        // capacity limit test
        assertEquals(capacityCount, getLocks().size());

        client.deleteExpiredLocks(-1000);
        assertTrue(getLocks().isEmpty());
    }

    @Test
    @DisplayName("测试超过客户端锁池容量上限的并发上锁解锁")
    public void testLockPoolCapacityExceededAgain() throws Exception {
        final int threadCount = 2;
        final int lockCount = 3;

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        client.setCacheCapacity(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final Queue<String> remainLockCheckQueue = new LinkedBlockingQueue<>();

        // 超出锁池容量上限时，客户端会根据加入锁池的先后顺序移除最老的锁
        for (int i = 0; i < lockCount; i++) {
            Lock getLockLock0 = client.getLock("test:" + i);
            getLockLock0.lock();
            getLockLock0.unlock();
        }

        for (int i = lockCount; i < threadCount + lockCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                } catch (InterruptedException ignored) {
                    log.error("testLockPoolCapacityExceededAgain error");
                }
                String keyId = "test:" + finalI;
                remainLockCheckQueue.offer(keyId);
                Lock getLock = client.getLock(keyId);
                getLock.lock();
                getLock.unlock();
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        for (String k : remainLockCheckQueue.toArray(new String[remainLockCheckQueue.size()])) {
            assertTrue(getLocks().containsKey(k));
        }
    }

    @Test
    @DisplayName("测试锁池容量动态增减")
    public void setLockPoolCapacityTest() throws Exception {
        final int capacityCnt = 4;
        client.setCacheCapacity(capacityCnt);

        client.getLock("test:1");
        client.getLock("test:2");
        client.getLock("test:3");

        // capacity 4->3
        client.setCacheCapacity(capacityCnt - 1);

        client.getLock("test:4");

        Map<String, DistributedLock> locks = getLocks();
        assertEquals(3, locks.size());
        assertTrue(locks.containsKey("test:2"));
        assertTrue(locks.containsKey("test:3"));
        assertTrue(locks.containsKey("test:4"));

        client.setCacheCapacity(capacityCnt);
        client.getLock("test:5");

        locks = getLocks();
        assertEquals(4, locks.size());
        assertTrue(locks.containsKey("test:3"));
        assertTrue(locks.containsKey("test:4"));
        assertTrue(locks.containsKey("test:5"));
    }

    private Map<String, DistributedLock> getLocks() throws Exception {
        Field locksField = DistributedLockClient.class.getDeclaredField("locks");
        locksField.setAccessible(true);
        if (!(locksField.get(client) instanceof Map)) {
            return new HashMap<>();
        }
        return (Map) locksField.get(client);
    }
}
