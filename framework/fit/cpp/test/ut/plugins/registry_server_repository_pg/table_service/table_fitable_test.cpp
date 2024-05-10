/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table fitable test
 * Author       : x00649642
 * Date         : 2023/12/04
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/registry_server/table_fitable.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Repository::Pg;
using FitableMeta = Fit::RegistryInfo::FitableMeta;
using Fitable = Fit::RegistryInfo::Fitable;
using Application = Fit::RegistryInfo::Application;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* GENERICABLE_ID = "genericable_id";
constexpr const char* GENERICABLE_VERSION = "genericable_version";
constexpr const char* FITABLE_ID = "fitable_id";
constexpr const char* FITABLE_VERSION = "fitable_version";
const Fit::vector<Fit::fit_format_type> FORMATS = {Fit::fit_format_type::PROTOBUF, Fit::fit_format_type::JSON};
constexpr const char* FORMATS_STR = "0,1";
constexpr const char* APPLICATION_NAME = "application_name";
constexpr const char* APPLICATION_VERSION = "application_version";
const Fit::vector<Fit::string> ALIASES = {"a", "b"};
constexpr const char* ALIASES_STR = "[\"a\",\"b\"]";
const Fit::vector<Fit::string> TAGS = {"t", "a", "g"};
constexpr const char* TAGS_STR = "[\"t\",\"a\",\"g\"]";
const Fit::map<Fit::string, Fit::string> EXTENSIONS = {{"key", "value"}};
constexpr const char* EXTENSIONS_STR = "{\"key\":\"value\"}";
constexpr const char* ENVIRONMENT = "environment";
Fit::vector<Fit::string> RESULT_STR{GENERICABLE_ID, GENERICABLE_VERSION, FITABLE_ID,          FITABLE_VERSION,
                                    FORMATS_STR,    APPLICATION_NAME,    APPLICATION_VERSION, ALIASES_STR,
                                    TAGS_STR,       EXTENSIONS_STR,      ENVIRONMENT};
}  // namespace

class TableFitableTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableFitable>();
        dummyApp.name = APPLICATION_NAME;
        dummyApp.nameVersion = APPLICATION_VERSION;

        dummyFitable.genericableId = GENERICABLE_ID;
        dummyFitable.genericableVersion = GENERICABLE_VERSION;
        dummyFitable.fitableId = FITABLE_ID;
        dummyFitable.fitableVersion = FITABLE_VERSION;

        dummyFitableMeta.application = dummyApp;
        dummyFitableMeta.formats = FORMATS;
        dummyFitableMeta.fitable = dummyFitable;
        dummyFitableMeta.aliases = ALIASES;
        dummyFitableMeta.tags = TAGS;
        dummyFitableMeta.extensions = EXTENSIONS;
        dummyFitableMeta.environment = ENVIRONMENT;
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
    Fit::unique_ptr<TableFitable> table;
    Application dummyApp;
    Fitable dummyFitable;
    FitableMeta dummyFitableMeta;
};

TEST_F(TableFitableTest, should_success_when_init)
{
    EXPECT_TRUE(table->Init());
}

TEST_F(TableFitableTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "insert into "
        "registry_fitable(genericable_id,genericable_version,fitable_id,fitable_version,formats,application_name,"
        "application_version,aliases,tags,extensions,environment) "
        "values ($1::varchar,$2::varchar,$3::varchar,$4::varchar,$5::varchar,$6::varchar,$7::varchar,$8::varchar,"
        "$9::varchar,$10::varchar,$11::varchar) on conflict on constraint registry_fitable_index do update set "
        "aliases=$12::varchar,environment=$13::varchar,extensions=$14::varchar,formats=$15::varchar,tags=$16::varchar";
    Fit::vector<const char*> sqlParams{GENERICABLE_ID, GENERICABLE_VERSION, FITABLE_ID,          FITABLE_VERSION,
                                       FORMATS_STR,    APPLICATION_NAME,    APPLICATION_VERSION, ALIASES_STR,
                                       TAGS_STR,       EXTENSIONS_STR,      ENVIRONMENT,         ALIASES_STR,
                                       ENVIRONMENT,    EXTENSIONS_STR,      FORMATS_STR,         TAGS_STR};
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

    ASSERT_EQ(table->Save(dummyFitableMeta), FIT_ERR_SUCCESS);
}

TEST_F(TableFitableTest, should_success_when_delete_by_application_success)
{
    constexpr const char* sqlCmdStr =
        "delete from registry_fitable where application_name=$1::varchar and application_version=$2::varchar";
    Fit::vector<const char*> sqlParams{APPLICATION_NAME, APPLICATION_VERSION};
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

    ASSERT_EQ(table->Delete(dummyApp), FIT_ERR_SUCCESS);
}

TEST_F(TableFitableTest, should_success_when_delete_by_fitable_meta_success)
{
    constexpr const char* sqlCmdStr =
        "delete from registry_fitable where genericable_id=$1::varchar and genericable_version=$2::varchar and "
        "fitable_id=$3::varchar and fitable_version=$4::varchar and application_name=$5::varchar and "
        "application_version=$6::varchar and aliases=$7::varchar and tags=$8::varchar and extensions=$9::varchar and "
        "environment=$10::varchar";
    Fit::vector<const char*> sqlParams{GENERICABLE_ID,   GENERICABLE_VERSION, FITABLE_ID,  FITABLE_VERSION,
                                       APPLICATION_NAME, APPLICATION_VERSION, ALIASES_STR, TAGS_STR,
                                       EXTENSIONS_STR,   ENVIRONMENT};
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

    ASSERT_EQ(table->Delete(dummyFitableMeta), FIT_ERR_SUCCESS);
}

TEST_F(TableFitableTest, should_success_return_multiple_rows_when_query_by_genericable_id_success)
{
    constexpr const char* sqlCmdStr = "select genericable_id, genericable_version, fitable_id, fitable_version, "
                                      "formats, application_name, application_version, aliases, tags, extensions, "
                                      "environment from registry_fitable where genericable_id=$1::varchar";
    Fit::vector<const char*> sqlParams{GENERICABLE_ID};
    Fit::vector<FitableMeta> resultCollector;
    const size_t resultCount = 2;
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

    resultCollector = table->Query(GENERICABLE_ID);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable.genericableId, GENERICABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.genericableVersion, GENERICABLE_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable.fitableId, FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.fitableVersion, FITABLE_VERSION);

        EXPECT_EQ(resultCollector[idx].formats, FORMATS);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);
        EXPECT_THAT(resultCollector[idx].aliases, ::testing::ContainerEq(ALIASES));
        EXPECT_THAT(resultCollector[idx].tags, ::testing::ContainerEq(TAGS));
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
        EXPECT_STRCASEEQ(resultCollector[idx].environment.c_str(), ENVIRONMENT);
    }
}

TEST_F(TableFitableTest, should_success_return_multiple_rows_when_query_by_application_success)
{
    constexpr const char* sqlCmdStr =
        "select genericable_id, genericable_version, fitable_id, fitable_version, "
        "formats, application_name, application_version, aliases, tags, extensions, "
        "environment from registry_fitable where application_name=$1::varchar and application_version=$2::varchar";
    Fit::vector<const char*> sqlParams{APPLICATION_NAME, APPLICATION_VERSION};
    Fit::vector<FitableMeta> resultCollector;
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

    resultCollector = table->Query(dummyApp);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable.genericableId, GENERICABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.genericableVersion, GENERICABLE_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable.fitableId, FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.fitableVersion, FITABLE_VERSION);

        EXPECT_EQ(resultCollector[idx].formats, FORMATS);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);
        EXPECT_THAT(resultCollector[idx].aliases, ::testing::ContainerEq(ALIASES));
        EXPECT_THAT(resultCollector[idx].tags, ::testing::ContainerEq(TAGS));
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
        EXPECT_STRCASEEQ(resultCollector[idx].environment.c_str(), ENVIRONMENT);
    }
}

TEST_F(TableFitableTest, should_success_return_multiple_rows_when_query_by_fitable_success)
{
    constexpr const char* sqlCmdStr =
        "select genericable_id, genericable_version, fitable_id, fitable_version, "
        "formats, application_name, application_version, aliases, tags, extensions, "
        "environment from registry_fitable where genericable_id=$1::varchar and genericable_version=$2::varchar and "
        "fitable_id=$3::varchar and fitable_version=$4::varchar";
    Fit::vector<const char*> sqlParams{GENERICABLE_ID, GENERICABLE_VERSION, FITABLE_ID, FITABLE_VERSION};
    Fit::vector<FitableMeta> resultCollector;
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

    resultCollector = table->Query(dummyFitable);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable.genericableId, GENERICABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.genericableVersion, GENERICABLE_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable.fitableId, FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.fitableVersion, FITABLE_VERSION);

        EXPECT_EQ(resultCollector[idx].formats, FORMATS);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);
        EXPECT_THAT(resultCollector[idx].aliases, ::testing::ContainerEq(ALIASES));
        EXPECT_THAT(resultCollector[idx].tags, ::testing::ContainerEq(TAGS));
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
        EXPECT_STRCASEEQ(resultCollector[idx].environment.c_str(), ENVIRONMENT);
    }
}

TEST_F(TableFitableTest, should_success_return_multiple_rows_when_query_by_fitable_meta_success)
{
    constexpr const char* sqlCmdStr =
        "select genericable_id, genericable_version, fitable_id, fitable_version, "
        "formats, application_name, application_version, aliases, tags, extensions, "
        "environment from registry_fitable where genericable_id=$1::varchar and genericable_version=$2::varchar and "
        "fitable_id=$3::varchar and fitable_version=$4::varchar and "
        "application_name=$5::varchar and application_version=$6::varchar and aliases=$7::varchar and "
        "tags=$8::varchar and extensions=$9::varchar and environment=$10::varchar";
    Fit::vector<const char*> sqlParams{GENERICABLE_ID,   GENERICABLE_VERSION, FITABLE_ID,  FITABLE_VERSION,
                                       APPLICATION_NAME, APPLICATION_VERSION, ALIASES_STR, TAGS_STR,
                                       EXTENSIONS_STR,   ENVIRONMENT};
    Fit::vector<FitableMeta> resultCollector;
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

    resultCollector = table->Query(dummyFitableMeta);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable.genericableId, GENERICABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.genericableVersion, GENERICABLE_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable.fitableId, FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.fitableVersion, FITABLE_VERSION);

        EXPECT_EQ(resultCollector[idx].formats, FORMATS);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);
        EXPECT_THAT(resultCollector[idx].aliases, ::testing::ContainerEq(ALIASES));
        EXPECT_THAT(resultCollector[idx].tags, ::testing::ContainerEq(TAGS));
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
        EXPECT_STRCASEEQ(resultCollector[idx].environment.c_str(), ENVIRONMENT);
    }
}

TEST_F(TableFitableTest, should_success_return_multiple_rows_when_query_all_success)
{
    constexpr const char* sqlCmdStr = "select genericable_id, genericable_version, fitable_id, fitable_version, "
                                      "formats, application_name, application_version, aliases, tags, extensions, "
                                      "environment from registry_fitable";
    Fit::vector<const char*> sqlParams{};
    Fit::vector<FitableMeta> resultCollector;
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
        EXPECT_EQ(resultCollector[idx].fitable.genericableId, GENERICABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.genericableVersion, GENERICABLE_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable.fitableId, FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].fitable.fitableVersion, FITABLE_VERSION);

        EXPECT_EQ(resultCollector[idx].formats, FORMATS);
        EXPECT_EQ(resultCollector[idx].application.name, APPLICATION_NAME);
        EXPECT_EQ(resultCollector[idx].application.nameVersion, APPLICATION_VERSION);
        EXPECT_THAT(resultCollector[idx].aliases, ::testing::ContainerEq(ALIASES));
        EXPECT_THAT(resultCollector[idx].tags, ::testing::ContainerEq(TAGS));
        EXPECT_THAT(resultCollector[idx].extensions, ::testing::ContainerEq(EXTENSIONS));
        EXPECT_STRCASEEQ(resultCollector[idx].environment.c_str(), ENVIRONMENT);
    }
}
