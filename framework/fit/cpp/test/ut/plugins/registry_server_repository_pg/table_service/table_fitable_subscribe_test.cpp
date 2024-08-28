/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table fitable subscribe test
 * Author       : x00649642
 * Date         : 2023/12/04
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/registry_server/table_fitable_subscribe.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Repository::Pg;
using Subscription = db_subscription_entry_t;
using Listener = fit_listener_info_t;
using Fitable = fit_fitable_key_t;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* ID = "subscriber_id";
constexpr const char* HOST = "subscriber_host";
constexpr uint32_t PORT = 10086;
constexpr const char* PORT_STR = "10086";
constexpr Fit::fit_protocol_type PROTOCOL = Fit::fit_protocol_type::HTTP;
constexpr const char* PROTOCOL_STR = "2";
constexpr const char* CALLBACK_FITABLE_ID = "subscriber_callback_fitid";
constexpr const char* SUBSCRIBED_GENERIC_ID = "subscribed_generic_id";
constexpr const char* SUBSCRIBED_GENERIC_VERSION = "subscribed_generic_version";
constexpr const char* SUBSCRIBED_FITABLE_ID = "subscribed_fitid";
Fit::vector<Fit::string> RESULT_STR{HOST,
                                    PORT_STR,
                                    PROTOCOL_STR,
                                    CALLBACK_FITABLE_ID,
                                    SUBSCRIBED_GENERIC_ID,
                                    SUBSCRIBED_GENERIC_VERSION,
                                    SUBSCRIBED_FITABLE_ID,
                                    ID};
Fit::vector<Fit::string> LISTENER_RESULT_STR{ID, HOST, PORT_STR, PROTOCOL_STR, CALLBACK_FITABLE_ID};
} // namespace

class TableFitableSubscribeTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableFitableSubscribe>();
        dummyFitable.generic_id = SUBSCRIBED_GENERIC_ID;
        dummyFitable.generic_version = SUBSCRIBED_GENERIC_VERSION;
        dummyFitable.fitable_id = SUBSCRIBED_FITABLE_ID;

        dummyListener.fitable_id = CALLBACK_FITABLE_ID;
        dummyListener.address.id = ID;
        dummyListener.address.ip = HOST;
        dummyListener.address.port = PORT;
        dummyListener.address.protocol = PROTOCOL;

        dummySubscription.fitable_key = dummyFitable;
        dummySubscription.listener = dummyListener;
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
    Fit::unique_ptr<TableFitableSubscribe> table;
    Subscription dummySubscription;
    Listener dummyListener;
    Fitable dummyFitable;
};

TEST_F(TableFitableSubscribeTest, should_success_when_start)
{
    EXPECT_TRUE(table->Start());
}

TEST_F(TableFitableSubscribeTest, should_success_when_stop)
{
    EXPECT_TRUE(table->Stop());
}

TEST_F(TableFitableSubscribeTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "insert into "
        "registry_fitable_subscribe(subscriber_host,subscriber_port,subscriber_protocol,subscriber_callback_fitid,"
        "subscribed_generic_id,subscribed_generic_version,subscribed_fitid,subscriber_id) values "
        "($1::varchar,$2::int,$3::int,$4::varchar,$5::varchar,$6::varchar,$7::varchar,$8::varchar) "
        "on conflict on constraint registry_fitable_subscribe_index do nothing";
    Fit::vector<const char*> sqlParams{HOST,
                                       PORT_STR,
                                       PROTOCOL_STR,
                                       CALLBACK_FITABLE_ID,
                                       SUBSCRIBED_GENERIC_ID,
                                       SUBSCRIBED_GENERIC_VERSION,
                                       SUBSCRIBED_FITABLE_ID,
                                       ID};
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

    ASSERT_EQ(table->insert_subscription_entry(dummyFitable, dummyListener), FIT_ERR_SUCCESS);
}

TEST_F(TableFitableSubscribeTest, should_success_when_delete_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "delete from registry_fitable_subscribe where "
        "subscriber_id=$1::varchar and subscriber_callback_fitid=$2::varchar and "
        "subscribed_generic_id=$3::varchar and subscribed_generic_version=$4::varchar and "
        "subscribed_fitid=$5::varchar";
    Fit::vector<const char*> sqlParams{ID, CALLBACK_FITABLE_ID, SUBSCRIBED_GENERIC_ID, SUBSCRIBED_GENERIC_VERSION,
                                       SUBSCRIBED_FITABLE_ID};
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

    ASSERT_EQ(table->remove_subscription_entry(dummyFitable, dummyListener), FIT_ERR_SUCCESS);
}

TEST_F(TableFitableSubscribeTest, should_success_return_multiple_rows_when_query_by_fitable_success)
{
    constexpr const char* sqlCmdStr =
        "select subscriber_host, subscriber_port, subscriber_protocol, "
        "subscriber_callback_fitid, subscribed_generic_id, subscribed_generic_version, "
        "subscribed_fitid, subscriber_id from registry_fitable_subscribe where "
        "subscribed_generic_id=$1::varchar and subscribed_generic_version=$2::varchar and subscribed_fitid=$3::varchar";
    Fit::vector<const char*> sqlParams = {SUBSCRIBED_GENERIC_ID, SUBSCRIBED_GENERIC_VERSION, SUBSCRIBED_FITABLE_ID};
    Fit::vector<Subscription> resultCollector;
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

    resultCollector = table->query_subscription_set(dummyFitable);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable_key.generic_id, SUBSCRIBED_GENERIC_ID);
        EXPECT_EQ(resultCollector[idx].fitable_key.generic_version, SUBSCRIBED_GENERIC_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable_key.fitable_id, SUBSCRIBED_FITABLE_ID);

        EXPECT_EQ(resultCollector[idx].listener.fitable_id, CALLBACK_FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].listener.address.id, ID);
        EXPECT_EQ(resultCollector[idx].listener.address.ip, HOST);
        EXPECT_EQ(resultCollector[idx].listener.address.port, PORT);
        EXPECT_EQ(resultCollector[idx].listener.address.protocol, PROTOCOL);
    }
}

TEST_F(TableFitableSubscribeTest, should_success_return_multiple_rows_when_query_all_success)
{
    constexpr const char* sqlCmdStr = "select subscriber_host, subscriber_port, subscriber_protocol, "
                                      "subscriber_callback_fitid, subscribed_generic_id, subscribed_generic_version, "
                                      "subscribed_fitid, subscriber_id from registry_fitable_subscribe";
    Fit::vector<const char*> sqlParams = {};
    Fit::vector<Subscription> resultCollector;
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

    resultCollector = table->query_all_subscriptions();
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable_key.generic_id, SUBSCRIBED_GENERIC_ID);
        EXPECT_EQ(resultCollector[idx].fitable_key.generic_version, SUBSCRIBED_GENERIC_VERSION);
        EXPECT_EQ(resultCollector[idx].fitable_key.fitable_id, SUBSCRIBED_FITABLE_ID);

        EXPECT_EQ(resultCollector[idx].listener.fitable_id, CALLBACK_FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].listener.address.id, ID);
        EXPECT_EQ(resultCollector[idx].listener.address.ip, HOST);
        EXPECT_EQ(resultCollector[idx].listener.address.port, PORT);
        EXPECT_EQ(resultCollector[idx].listener.address.protocol, PROTOCOL);
    }
}

TEST_F(TableFitableSubscribeTest, should_success_return_multiple_rows_when_query_listener_by_fitable_success)
{
    constexpr const char* sqlCmdStr =
        "select subscriber_id, subscriber_host, subscriber_port, subscriber_protocol, "
        "subscriber_callback_fitid from registry_fitable_subscribe where "
        "subscribed_generic_id=$1::varchar and subscribed_generic_version=$2::varchar and subscribed_fitid=$3::varchar";
    Fit::vector<const char*> sqlParams = {SUBSCRIBED_GENERIC_ID, SUBSCRIBED_GENERIC_VERSION, SUBSCRIBED_FITABLE_ID};
    Fit::vector<Listener> resultCollector;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol())
            .Times(AtLeast(1))
            .WillRepeatedly(Return(LISTENER_RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(LISTENER_RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->query_listener_set(dummyFitable);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(resultCollector[idx].fitable_id, CALLBACK_FITABLE_ID);
        EXPECT_EQ(resultCollector[idx].address.id, ID);
        EXPECT_EQ(resultCollector[idx].address.ip, HOST);
        EXPECT_EQ(resultCollector[idx].address.port, PORT);
        EXPECT_EQ(resultCollector[idx].address.protocol, PROTOCOL);
    }
}

TEST_F(TableFitableSubscribeTest, should_success_return_1_row_when_query_by_fitable_and_listener_success)
{
    constexpr const char* sqlCmdStr =
        "select subscriber_host, subscriber_port, subscriber_protocol, "
        "subscriber_callback_fitid, subscribed_generic_id, subscribed_generic_version, subscribed_fitid, subscriber_id "
        "from registry_fitable_subscribe where "
        "subscriber_id=$1::varchar and subscriber_callback_fitid=$2::varchar and "
        "subscribed_generic_id=$3::varchar and subscribed_generic_version=$4::varchar and subscribed_fitid=$5::varchar";
    Fit::vector<const char*> sqlParams = {ID, CALLBACK_FITABLE_ID, SUBSCRIBED_GENERIC_ID, SUBSCRIBED_GENERIC_VERSION,
                                          SUBSCRIBED_FITABLE_ID};
    Subscription result;
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(1));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(1).WillOnce(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->query_subscription_entry(dummyFitable, dummyListener, result), FIT_ERR_SUCCESS);
    EXPECT_EQ(result.fitable_key.generic_id, SUBSCRIBED_GENERIC_ID);
    EXPECT_EQ(result.fitable_key.generic_version, SUBSCRIBED_GENERIC_VERSION);
    EXPECT_EQ(result.fitable_key.fitable_id, SUBSCRIBED_FITABLE_ID);

    EXPECT_EQ(result.listener.fitable_id, CALLBACK_FITABLE_ID);
    EXPECT_EQ(result.listener.address.id, ID);
    EXPECT_EQ(result.listener.address.ip, HOST);
    EXPECT_EQ(result.listener.address.port, PORT);
    EXPECT_EQ(result.listener.address.protocol, PROTOCOL);
}
