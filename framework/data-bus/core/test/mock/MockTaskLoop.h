/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : Mock TaskLoop
 */

#ifndef DATABUS_MOCK_TASK_LOOP_H
#define DATABUS_MOCK_TASK_LOOP_H

#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include "TaskLoop.h"

namespace DataBus {
namespace Test {

class MockTaskLoop : public Task::TaskLoop {
public:
    MOCK_METHOD(void, AddOpenTask, (int), (override));
    MOCK_METHOD(void, AddCloseTask, (int), (override));
    MOCK_METHOD(void, AddReadTask, (int, const char*, size_t), (override));
    MOCK_METHOD(void, AddWriteTask, (int, const char*, size_t), (override));
    MOCK_METHOD(std::unique_ptr<Task::Task>, GetNextTask, (), (override));
};
} // namespace Test
} // namespace DataBus

#endif // DATABUS_MOCK_TASK_LOOP_H
