/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: test for common/stl/BlockingQueue
 */

#include <thread>
#include <chrono>
#include <atomic>

#include <gtest/gtest.h>

#include "BlockingQueue.h"

using namespace std;

namespace DataBus {
namespace Test {
class BlockingQueueTest : public testing::Test {
};
  
TEST_F(BlockingQueueTest, EnqueueDequeue)
{
    const int testNum = 42;
    const int queueSize = 10;
    DataBus::Stl::BlockingQueue<int> queue{queueSize};
    EXPECT_EQ(0, queue.size());
    queue.enqueue(testNum);
    EXPECT_EQ(1, queue.size());
    auto value = queue.dequeue();
    EXPECT_EQ(queueSize, queue.capacity());
    EXPECT_EQ(0, queue.size());
    EXPECT_EQ(testNum, value);
}
  
TEST_F(BlockingQueueTest, BlockingDequeue)
{
    const int queueSize = 10;
    DataBus::Stl::BlockingQueue<int> queue{queueSize};
    const int testNum = 42;
    const int sleepTime = 100;
    atomic<bool> dequeued{false};
    atomic<int> dequeueValue{0};
    EXPECT_TRUE(queue.empty());

    thread dequeuer([&queue, &dequeueValue, &dequeued]() {
        dequeueValue = queue.dequeue();
        EXPECT_TRUE(queue.empty());
        dequeued = true;
    });

    std::this_thread::sleep_for(std::chrono::milliseconds(sleepTime));
    queue.enqueue(testNum);
    dequeuer.join();

    EXPECT_TRUE(queue.empty());
    EXPECT_TRUE(dequeued);
    EXPECT_EQ(testNum, dequeueValue);
    EXPECT_EQ(testNum, dequeueValue);
}
}  // namespace Test
}  // namespace DataBus
