/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq connection pool test
 * Author       : x00649642
 * Date         : 2023/11/29
 */
#include <thread>
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/sql_wrapper/sql_cmd.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"

using namespace Fit::Repository::Pg;

class ConnectionPoolTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
    }

    void TearDown() override
    {
        ConnectionPool::Instance().TearDown();
        ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::INIT);
        const ::testing::TestInfo* const test_info = ::testing::UnitTest::GetInstance()->current_test_info();
        auto capture = testing::internal::GetCapturedStdout();
        if (test_info->result()->Failed()) {
            std::cout << std::endl
                      << "Captured output from failed test" << std::endl
                      << test_info->test_suite_name() << "." << test_info->name() << std::endl
                      << capture << std::endl;
        }
    }
};

TEST_F(ConnectionPoolTest, should_fail_create_expect_number_connections_when_call_setup_with_0_retry)
{
    // given
    constexpr size_t badParam = 0;
    auto connectionCreator = [](const char*) { return Fit::make_unique<SqlConnectionMock>(); };

    // when
    auto returnCode = ConnectionPool::Instance().SetUp("", 1, badParam, connectionCreator);

    // then
    EXPECT_EQ(returnCode, FIT_ERR_PARAM);
}

TEST_F(ConnectionPoolTest, should_fail_create_expect_number_connections_when_call_setup_in_ready_status)
{
    // given
    ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::READY);

    // when
    auto returnCode =
        ConnectionPool::Instance().SetUp("", 1, 1, [](const char*) { return Fit::make_unique<SqlConnectionMock>(); });

    // then
    EXPECT_EQ(returnCode, FIT_ERR_FAIL);
}

TEST_F(ConnectionPoolTest, should_fail_create_expect_number_connections_when_call_setup_in_terminate_status)
{
    // given
    ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::TERMINATE);

    // when
    auto returnCode =
        ConnectionPool::Instance().SetUp("", 1, 1, [](const char*) { return Fit::make_unique<SqlConnectionMock>(); });

    // then
    EXPECT_EQ(returnCode, FIT_ERR_FAIL);
}

TEST_F(ConnectionPoolTest, should_success_create_expect_number_connections_when_call_setup_with_correct_param)
{
    // given
    size_t expectConnectionNum = 2;

    // when
    auto returnCode = ConnectionPool::Instance().SetUp("", expectConnectionNum, 1, [](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(::testing::Return(true));
        return connectionMock;
    });

    // then
    EXPECT_EQ(returnCode, FIT_ERR_SUCCESS);
    EXPECT_EQ(ConnectionPool::Instance().GetAvailableConnectionCount(), expectConnectionNum);
    EXPECT_EQ(ConnectionPool::Instance().GetTotalConnectionCount(), expectConnectionNum);
}

TEST_F(ConnectionPoolTest, should_success_create_expect_number_connections_when_call_setup_with_sufficient_retry)
{
    // given
    size_t expectConnectionNum = 4;
    size_t retryTimes = 5;
    // fail retry - 1 times, then success all way through
    // in such case will create expectNum
    size_t failTimes = retryTimes - 1;
    bool doCreate = false;
    auto connectionCreator = [this, &doCreate, &failTimes](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();
        doCreate = (failTimes > 0);
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(::testing::Return(doCreate));
        if (!doCreate) {
            --failTimes;
            EXPECT_CALL(*connectionMock, GetErrorMessage()).Times(1).WillOnce(::testing::Return("fake error"));
        }
        return connectionMock;
    };

    // when
    auto returnCode = ConnectionPool::Instance().SetUp("", expectConnectionNum, retryTimes, connectionCreator);

    // then
    EXPECT_EQ(returnCode, FIT_ERR_SUCCESS);
    EXPECT_EQ(ConnectionPool::Instance().GetAvailableConnectionCount(), expectConnectionNum);
    EXPECT_EQ(ConnectionPool::Instance().GetTotalConnectionCount(), expectConnectionNum);
}

TEST_F(ConnectionPoolTest,
    should_success_create_less_than_expect_connections_when_call_setup_with_insufficient_retry)
{
    // given
    size_t presetConnectionNum = 3;
    size_t expectConnectionNum = 1;
    size_t retryTimes = 5;
    // success only once
    bool doCreate = true;
    auto connectionCreator = [this, &doCreate](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(::testing::Return(doCreate));
        if (doCreate) {
            doCreate = false;
        } else {
            EXPECT_CALL(*connectionMock, GetErrorMessage()).Times(1).WillOnce(::testing::Return("fake error"));
        }
        return connectionMock;
    };

    // when
    auto returnCode = ConnectionPool::Instance().SetUp("", presetConnectionNum, retryTimes, connectionCreator);

    // then
    EXPECT_EQ(returnCode, FIT_ERR_SUCCESS);
    EXPECT_EQ(ConnectionPool::Instance().GetAvailableConnectionCount(), expectConnectionNum);
    EXPECT_EQ(ConnectionPool::Instance().GetTotalConnectionCount(), expectConnectionNum);
}

TEST_F(ConnectionPoolTest, should_fail_when_call_submit_in_init_status)
{
    // given
    ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::INIT);

    // then
    EXPECT_EQ(nullptr,
              ConnectionPool::Instance().Submit([](SqlConnectionPtr&) -> SqlExecResultPtr { return nullptr; }));
    EXPECT_EQ(nullptr, ConnectionPool::Instance().Submit(Fit::Pg::SqlCmd()));
}

TEST_F(ConnectionPoolTest, should_fail_when_call_submit_in_terminate_status)
{
    // given
    ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::TERMINATE);

    // then
    EXPECT_EQ(nullptr,
              ConnectionPool::Instance().Submit([](SqlConnectionPtr&) -> SqlExecResultPtr { return nullptr; }));
    EXPECT_EQ(nullptr, ConnectionPool::Instance().Submit(Fit::Pg::SqlCmd()));
}

TEST_F(ConnectionPoolTest, should_success_call_exec_param_when_call_submit_with_sql_cmd)
{
    // given
    constexpr const char* sqlCmdStr = "sql cmd";
    Fit::vector<const char*> sqlParams = {"param1", "param2"};
    Fit::Pg::SqlCmd sqlCmd{sqlCmdStr, sqlParams, {}};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();

        using namespace ::testing;
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), ContainerEq(sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(nullptr)));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ConnectionPool::Instance().Submit(sqlCmd);
}

TEST_F(ConnectionPoolTest, should_success_call_exec_param_when_call_submit_with_sql_cmd_in_parallel)
{
    // given
    constexpr const char* sqlCmdStr = "sql cmd";
    Fit::vector<const char*> sqlParams = {"param1", "param2"};
    Fit::Pg::SqlCmd sqlCmd{sqlCmdStr, sqlParams, {}};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();

        using namespace ::testing;
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), ContainerEq(sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(nullptr)));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 2, 1, connectionCreator);

    std::thread t1([this, sqlCmd]() { ConnectionPool::Instance().Submit(sqlCmd); });
    std::thread t2([this, sqlCmd]() { ConnectionPool::Instance().Submit(sqlCmd); });
    t1.join();
    t2.join();
}

TEST_F(ConnectionPoolTest, should_success_set_terminate_and_clear_queue_when_call_tear_down)
{
    // given
    auto connectionCreator = [this](const char*) { return Fit::make_unique<SqlConnectionMock>(); };
    ConnectionPool::Instance().SetUp("", 2, 1, connectionCreator);

    // when
    ConnectionPool::Instance().TearDown();

    // then
    EXPECT_EQ(ConnectionPool::Instance().GetStatus(), ConnectionPoolStatus::TERMINATE);
    EXPECT_EQ(ConnectionPool::Instance().GetAvailableConnectionCount(), 0);
    EXPECT_EQ(ConnectionPool::Instance().GetTotalConnectionCount(), 0);
}
