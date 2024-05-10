/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table worker test
 * Author       : x00649642
 * Date         : 2023/12/04
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/registry_server/table_worker.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Repository::Pg;
using Worker = Fit::RegistryInfo::Worker;
using Application = Fit::RegistryInfo::Application;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* WORKER_ID = "worker_id";
constexpr const char* APPLICATION_NAME = "application_name";
constexpr const char* APPLICATION_VERSION = "application_version";
constexpr uint64_t EXPIRE = 777;
constexpr const char* EXPIRE_STR = "777";
constexpr const char* ENVIRONMENT = "environment";
constexpr const char* WORKER_VERSION = "worker_version";
const Fit::map<Fit::string, Fit::string> EXTENSIONS = {{"key", "value"}};
constexpr const char* EXTENSIONS_STR = "{\"key\":\"value\"}";

Fit::vector<Fit::string> RESULT_STR{WORKER_ID,   APPLICATION_NAME, APPLICATION_VERSION, EXPIRE_STR,
                                    ENVIRONMENT, WORKER_VERSION,   EXTENSIONS_STR};
} // namespace

class TableWorkerTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableWorker>();
        dummyApp.name = APPLICATION_NAME;
        dummyApp.nameVersion = APPLICATION_VERSION;

        dummyWorker.application = dummyApp;
        dummyWorker.workerId = WORKER_ID;
        dummyWorker.expire = EXPIRE;
        dummyWorker.environment = ENVIRONMENT;
        dummyWorker.version = WORKER_VERSION;
        dummyWorker.extensions = EXTENSIONS;
    }

    void TearDown() override
    {
        ConnectionPool::Instance().TearDown();
        ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::INIT);
        table.reset();
        const ::testing::TestInfo* const test_info = ::testing::UnitTest::GetInstance()->current_test_info();
        auto capture = testing::internal::GetCapturedStdout();
        if (test_info->result()->Failed()) {
            std::cout << std::endl
                      << "Captured output from failed test" << std::endl
                      << test_info->test_suite_name() << "." << test_info->name() << std::endl
                      << capture << std::endl;
        }
    }

public:
    Fit::unique_ptr<TableWorker> table;
    Application dummyApp;
    Worker dummyWorker;
};

TEST_F(TableWorkerTest, should_success_when_init)
{
    EXPECT_TRUE(table->Init());
}

TEST_F(TableWorkerTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "insert into "
        "registry_worker(worker_id,application_name,application_version,expire,environment,worker_version,extensions) "
        "values ($1::varchar,$2::varchar,$3::varchar,$4::int,$5::varchar,$6::varchar,$7::varchar) on conflict on "
        "constraint registry_worker_index do update set "
        "environment=$8::varchar,expire=$9::int,extensions=$10::varchar,worker_version=$11::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID,   APPLICATION_NAME, APPLICATION_VERSION, EXPIRE_STR,
                                       ENVIRONMENT, WORKER_VERSION,   EXTENSIONS_STR,      ENVIRONMENT,
                                       EXPIRE_STR,  EXTENSIONS_STR,   WORKER_VERSION};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(AtLeast(1)).WillRepeatedly(Return(1));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->Save(dummyWorker), FIT_ERR_SUCCESS);
}

TEST_F(TableWorkerTest, should_success_when_delete_by_worker_id_success)
{
    constexpr const char* sqlCmdStr = "delete from registry_worker where worker_id=$1::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->Delete(WORKER_ID), FIT_ERR_SUCCESS);
}

TEST_F(TableWorkerTest, should_success_when_delete_by_worker_id_and_application_success)
{
    constexpr const char* sqlCmdStr = "delete from registry_worker where worker_id=$1::varchar and "
                                      "application_name=$2::varchar and application_version=$3::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID, APPLICATION_NAME, APPLICATION_VERSION};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->Delete(WORKER_ID, dummyApp), FIT_ERR_SUCCESS);
}

TEST_F(TableWorkerTest, should_success_return_multiple_rows_when_query_by_worker_id_success)
{
    constexpr const char* sqlCmdStr = "select worker_id, application_name, application_version, expire, environment, "
                                      "worker_version, extensions from registry_worker where worker_id=$1::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID};
    Fit::vector<Worker> resultCollector;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->Query(WORKER_ID);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].workerId, WORKER_ID);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);

        EXPECT_EQ(resultCollector[idx].expire, EXPIRE);
        EXPECT_EQ(resultCollector[idx].environment, ENVIRONMENT);
        EXPECT_EQ(resultCollector[idx].version, WORKER_VERSION);
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
    }
}

TEST_F(TableWorkerTest, should_success_return_multiple_rows_when_query_by_application_success)
{
    constexpr const char* sqlCmdStr = "select worker_id, application_name, application_version, expire, environment, "
                                      "worker_version, extensions from registry_worker where "
                                      "application_name=$1::varchar and application_version=$2::varchar";
    Fit::vector<const char*> sqlParams{APPLICATION_NAME, APPLICATION_VERSION};
    Fit::vector<Worker> resultCollector;
    const size_t resultCount = 4;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->Query(dummyApp);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].workerId, WORKER_ID);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);

        EXPECT_EQ(resultCollector[idx].expire, EXPIRE);
        EXPECT_EQ(resultCollector[idx].environment, ENVIRONMENT);
        EXPECT_EQ(resultCollector[idx].version, WORKER_VERSION);
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
    }
}

TEST_F(TableWorkerTest, should_success_return_multiple_rows_when_query_all_success)
{
    constexpr const char* sqlCmdStr = "select worker_id, application_name, application_version, expire, environment, "
                                      "worker_version, extensions from registry_worker";
    Fit::vector<const char*> sqlParams{};
    Fit::vector<Worker> resultCollector;
    const size_t resultCount = 5;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->QueryAll();
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].workerId, WORKER_ID);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);

        EXPECT_EQ(resultCollector[idx].expire, EXPIRE);
        EXPECT_EQ(resultCollector[idx].environment, ENVIRONMENT);
        EXPECT_EQ(resultCollector[idx].version, WORKER_VERSION);
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
    }
}
