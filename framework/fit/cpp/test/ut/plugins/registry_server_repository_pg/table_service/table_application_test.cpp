/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table application test
 * Author       : x00649642
 * Date         : 2023/12/01
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/registry_server/table_application.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Repository::Pg;
using Application = Fit::RegistryInfo::Application;
using ApplicationMeta = Fit::RegistryInfo::ApplicationMeta;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* APP_NAME = "app name";
constexpr const char* APP_VER = "app version";
const Fit::map<Fit::string, Fit::string> EXTENSIONS = {{"key", "value"}};
constexpr const char* EXTENSIONS_STR = "{\"key\":\"value\"}";
Fit::vector<Fit::string> RESULT_STR{APP_NAME, APP_VER, EXTENSIONS_STR};
}  // namespace

class TableApplicationTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableApplication>();
        dummyApp.name = APP_NAME;
        dummyApp.nameVersion = APP_VER;
        dummyAppMeta.id = dummyApp;
        dummyAppMeta.extensions = EXTENSIONS;
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
    Fit::unique_ptr<TableApplication> table;
    Application dummyApp;
    ApplicationMeta dummyAppMeta;
};

TEST_F(TableApplicationTest, should_success_when_get_name)
{
    EXPECT_STRCASEEQ(table->GetName(), "pgRepo");
}

TEST_F(TableApplicationTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlQueryCmdStr =
        "select application_name, application_version, extensions from registry_application where "
        "application_name=$1::varchar and application_version=$2::varchar";
    constexpr const char* sqlCmdStr =
        "insert into registry_application(application_name,application_version,extensions) values "
        "($1::varchar,$2::varchar,$3::varchar) on conflict on constraint registry_application_index do update set "
        "extensions=$4::varchar";
    Fit::vector<const char*> sqlQueryParams = {APP_NAME, APP_VER};
    Fit::vector<const char*> sqlParams{APP_NAME, APP_VER, EXTENSIONS_STR, EXTENSIONS_STR};
    auto connectionCreator = [this, sqlQueryCmdStr, sqlQueryParams, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();
        auto sqlQueryExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;

        EXPECT_CALL(*sqlQueryExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlQueryExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(0));
        EXPECT_CALL(*sqlQueryExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));

        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlQueryCmdStr),
            Pointwise(IsTupleSameString(), sqlQueryParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlQueryExecResultMock))));

        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(AtLeast(1)).WillRepeatedly(Return(1));

        EXPECT_CALL(*connectionMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    EXPECT_EQ(table->Save(dummyAppMeta), FIT_ERR_SUCCESS);
}

TEST_F(TableApplicationTest, should_success_when_delete_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "delete from registry_application where application_name=$1::varchar and application_version=$2::varchar";
    Fit::vector<const char*> sqlParams{APP_NAME, APP_VER};
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

    EXPECT_EQ(table->Delete(dummyApp), FIT_ERR_SUCCESS);
}

TEST_F(TableApplicationTest, should_success_return_multiple_rows_when_query_by_app_name_success)
{
    constexpr const char* sqlCmdStr = "select application_name, application_version, extensions from "
                                      "registry_application where application_name=$1::varchar";
    Fit::vector<const char*> sqlParams = {APP_NAME};
    Fit::vector<ApplicationMeta> resultCollector;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
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

    resultCollector = table->Query(APP_NAME);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].id.name, APP_NAME);
        EXPECT_EQ(resultCollector[idx].id.nameVersion, APP_VER);
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
    }
}

TEST_F(TableApplicationTest, should_success_return_1_row_when_query_by_app_success)
{
    constexpr const char* sqlCmdStr =
        "select application_name, application_version, extensions from registry_application where "
        "application_name=$1::varchar and application_version=$2::varchar";
    Fit::vector<const char*> sqlParams = {APP_NAME, APP_VER};
    ApplicationMeta resultCollector;
    const size_t resultCount = 1;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
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

    ASSERT_EQ(table->Query(dummyApp, resultCollector), FIT_ERR_SUCCESS);
    EXPECT_EQ(resultCollector.id.name, APP_NAME);
    EXPECT_EQ(resultCollector.id.nameVersion, APP_VER);
    EXPECT_THAT(resultCollector.extensions, ::testing::ContainerEq(EXTENSIONS));
}

TEST_F(TableApplicationTest, should_success_return_multiple_rows_when_query_all_success)
{
    constexpr const char* sqlCmdStr =
        "select application_name, application_version, extensions from registry_application";
    Fit::vector<Fit::string> sqlResultStr{APP_NAME, APP_VER, EXTENSIONS_STR};
    Fit::vector<ApplicationMeta> resultCollector;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), IsEmpty()))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->QueryAll();
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].id.name, APP_NAME);
        EXPECT_EQ(resultCollector[idx].id.nameVersion, APP_VER);
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
    }
}
