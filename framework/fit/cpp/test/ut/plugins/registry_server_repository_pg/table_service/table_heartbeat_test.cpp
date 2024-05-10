/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table heartbeat test
 * Author       : x00649642
 * Date         : 2023/12/04
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/heartbeat_server/table_heartbeat.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Pg;
using AddressStatusInfo = Fit::Heartbeat::AddressStatusInfo;
using AddressBeatInfo = Fit::Heartbeat::AddressBeatInfo;
using BeatInfo = Fit::Heartbeat::BeatInfo;

bool operator!=(const Fit::fit_address& lhs, const Fit::fit_address& rhs)
{
    return std::tie(lhs.ip, lhs.port, lhs.protocol, lhs.formats, lhs.environment) !=
           std::tie(rhs.ip, rhs.port, rhs.protocol, rhs.formats, rhs.environment);
}

bool operator==(const BeatInfo& lhs, const BeatInfo& rhs)
{
    return std::tie(lhs.sceneType, lhs.aliveTime, lhs.interval, lhs.initDelay, lhs.callbackFitId) ==
           std::tie(rhs.sceneType, rhs.aliveTime, rhs.interval, rhs.initDelay, rhs.callbackFitId);
}

bool operator==(const AddressBeatInfo& lhs, const AddressBeatInfo& rhs)
{
    bool addressesSame = (lhs.id == rhs.id && lhs.addresses.size() == rhs.addresses.size());
    if (!addressesSame) {
        return false;
    }
    for (size_t idx = 0; idx < lhs.addresses.size(); ++idx) {
        if (lhs.addresses[idx] != rhs.addresses[idx]) {
            return false;
        }
    }
    return lhs.beat_info == rhs.beat_info;
}

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

MATCHER_P(IsEqualStatusInfo, rhs, "")
{
    if (arg.addressBeatInfo.id != rhs.addressBeatInfo.id) {
        *result_listener << "addressBeatInfo fields are different, where \nexpect: " << ::testing::PrintToString(rhs)
                         << "\nactual: " << ::testing::PrintToString(arg);
        return false;
    }
    if (arg.addressBeatInfo.addresses.size() != rhs.addressBeatInfo.addresses.size()) {
        *result_listener << "addressBeatInfo.addresses sizes are different, where \nexpect: "
                         << ::testing::PrintToString(arg.addressBeatInfo.addresses.size())
                         << "\nactual: " << ::testing::PrintToString(rhs.addressBeatInfo.addresses.size());
        return false;
    }
    for (size_t idx = 0; idx < rhs.addressBeatInfo.addresses.size(); ++idx) {
        if (arg.addressBeatInfo.addresses[idx] != rhs.addressBeatInfo.addresses[idx]) {
            *result_listener << "addressBeatInfo.addresses at index " << idx << " are different, where \nexpect: "
                             << ::testing::PrintToString(arg.addressBeatInfo.addresses[idx])
                             << "\nactual: " << ::testing::PrintToString(rhs.addressBeatInfo.addresses[idx]);
            return false;
        }
    }
    return arg.addressBeatInfo.beat_info == rhs.addressBeatInfo.beat_info &&
           std::tie(arg.start_time, arg.last_heartbeat_time, arg.expired_time, arg.status) ==
               std::tie(rhs.start_time, rhs.last_heartbeat_time, rhs.expired_time, rhs.status);
}

namespace {
constexpr const char* SCENE_TYPE = "scene_type";
constexpr const char* ID = "id";
constexpr int64_t ALIVE_TIME = 10086;
constexpr const char* ALIVE_TIME_STR = "10086";
constexpr int64_t INTERVAL = 10010;
constexpr const char* INTERVAL_STR = "10010";
constexpr int64_t INIT_DELAY = 10000;
constexpr const char* INIT_DELAY_STR = "10000";
constexpr const char* CALLBACK_FITID = "callback_fitid";
constexpr int64_t START_TIME = 95533;
constexpr const char* START_TIME_STR = "95533";
constexpr int64_t LAST_HEARTBEAT_TIME = 95555;
constexpr const char* LAST_HEARTBEAT_TIME_STR = "95555";
constexpr int64_t EXPIRED_TIME = 95566;
constexpr const char* EXPIRED_TIME_STR = "95566";
constexpr Fit::Heartbeat::HeartbeatStatus STATUS = Fit::Heartbeat::HeartbeatStatus::ALIVE;
constexpr const char* STATUS_STR = "1";
constexpr const char* HOST = "host";
constexpr uint32_t PORT = 8080;
constexpr const char* PORT_STR = "8080";
constexpr Fit::fit_protocol_type PROTOCOL = Fit::fit_protocol_type::HTTP;
constexpr const char* PROTOCOL_STR = "2";
const Fit::vector<Fit::fit_format_type> FORMATS = {Fit::fit_format_type::PROTOBUF, Fit::fit_format_type::JSON};
constexpr const char* FORMATS_STR = "0,1";
constexpr const char* ENVIRONMENT = "environment";

Fit::vector<Fit::string> RESULT_STR{SCENE_TYPE,
                                    ID,
                                    ALIVE_TIME_STR,
                                    INTERVAL_STR,
                                    INIT_DELAY_STR,
                                    CALLBACK_FITID,
                                    START_TIME_STR,
                                    LAST_HEARTBEAT_TIME_STR,
                                    EXPIRED_TIME_STR,
                                    STATUS_STR,
                                    HOST,
                                    PORT_STR,
                                    PROTOCOL_STR,
                                    FORMATS_STR,
                                    ENVIRONMENT};
}  // namespace

class TableHeartbeatTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableHeartbeat>(&ConnectionPool::Instance());
        dummyAddress.ip = HOST;
        dummyAddress.port = PORT;
        dummyAddress.protocol = PROTOCOL;
        dummyAddress.formats = FORMATS;
        dummyAddress.environment = ENVIRONMENT;

        dummyBeatInfo.sceneType = SCENE_TYPE;
        dummyBeatInfo.aliveTime = ALIVE_TIME;
        dummyBeatInfo.interval = INTERVAL;
        dummyBeatInfo.initDelay = INIT_DELAY;
        dummyBeatInfo.callbackFitId = CALLBACK_FITID;

        dummyAddressBeatInfo.id = ID;
        dummyAddressBeatInfo.addresses.emplace_back(dummyAddress);
        dummyAddressBeatInfo.beat_info = dummyBeatInfo;

        dummyStatusInfo.addressBeatInfo = dummyAddressBeatInfo;
        dummyStatusInfo.start_time = START_TIME;
        dummyStatusInfo.last_heartbeat_time = LAST_HEARTBEAT_TIME;
        dummyStatusInfo.expired_time = EXPIRED_TIME;
        dummyStatusInfo.status = STATUS;
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
    Fit::unique_ptr<TableHeartbeat> table;
    AddressStatusInfo dummyStatusInfo;
    AddressBeatInfo dummyAddressBeatInfo;
    BeatInfo dummyBeatInfo;
    Fit::fit_address dummyAddress;
};

TEST_F(TableHeartbeatTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "insert into "
        "registry_heartbeat(scene_type,id,alive_time,interval,init_delay,callback_fitid,start_time,last_heartbeat_time,"
        "expired_time,status,host,port,protocol,formats,environment) values "
        "($1::varchar,$2::varchar,$3::int,$4::int,$5::int,$6::varchar,$7::bigint,$8::bigint,$9::bigint,$10::varchar,"
        "$11::varchar,$12::int,$13::smallint,$14::varchar,$15::varchar)";
    Fit::vector<const char*> sqlParams{SCENE_TYPE,
                                       ID,
                                       ALIVE_TIME_STR,
                                       INTERVAL_STR,
                                       INIT_DELAY_STR,
                                       CALLBACK_FITID,
                                       START_TIME_STR,
                                       LAST_HEARTBEAT_TIME_STR,
                                       EXPIRED_TIME_STR,
                                       STATUS_STR,
                                       HOST,
                                       PORT_STR,
                                       PROTOCOL_STR,
                                       FORMATS_STR,
                                       ENVIRONMENT};
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

    ASSERT_EQ(table->add_beat(dummyStatusInfo), FIT_ERR_SUCCESS);
}

TEST_F(TableHeartbeatTest, should_success_when_modify_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "update registry_heartbeat set "
        "alive_time=$1::int,interval=$2::int,init_delay=$3::int,callback_fitid=$4::varchar,last_heartbeat_time=$5::"
        "bigint,start_time=$6::bigint,expired_time=$7::bigint,status=$8::varchar,host=$9::varchar,port=$10::int,"
        "protocol=$11::smallint,formats=$12::varchar,environment=$13::varchar where id=$14::varchar and "
        "scene_type=$15::varchar";
    Fit::vector<const char*> sqlParams{ALIVE_TIME_STR,
                                       INTERVAL_STR,
                                       INIT_DELAY_STR,
                                       CALLBACK_FITID,
                                       LAST_HEARTBEAT_TIME_STR,
                                       START_TIME_STR,
                                       EXPIRED_TIME_STR,
                                       STATUS_STR,
                                       HOST,
                                       PORT_STR,
                                       PROTOCOL_STR,
                                       FORMATS_STR,
                                       ENVIRONMENT,
                                       ID,
                                       SCENE_TYPE};
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

    ASSERT_EQ(table->modify_beat(dummyStatusInfo), FIT_ERR_SUCCESS);
}

TEST_F(TableHeartbeatTest, should_success_when_delete_1_entity_success)
{
    constexpr const char* sqlCmdStr = "delete from registry_heartbeat where id=$1::varchar and scene_type=$2::varchar";
    Fit::vector<const char*> sqlParams{ID, SCENE_TYPE};
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

    ASSERT_EQ(table->remove_beat(dummyAddressBeatInfo), FIT_ERR_SUCCESS);
}

TEST_F(TableHeartbeatTest, should_success_return_1_row_when_query_by_worker_id_success)
{
    constexpr const char* sqlCmdStr =
        "select scene_type, id, alive_time, interval, init_delay, callback_fitid, start_time, "
        "last_heartbeat_time, expired_time, status, host, port, protocol, formats, "
        "environment from registry_heartbeat where id=$1::varchar and scene_type=$2::varchar";
    Fit::vector<const char*> sqlParams{ID, SCENE_TYPE};
    AddressStatusInfo resultCollector;
    const size_t resultCount = 1;
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

    ASSERT_EQ(table->query_beat(dummyAddressBeatInfo, resultCollector), FIT_ERR_SUCCESS);
    EXPECT_THAT(resultCollector, IsEqualStatusInfo(dummyStatusInfo));
}

TEST_F(TableHeartbeatTest, should_success_return_multiple_rows_when_query_all_success)
{
    constexpr const char* sqlCmdStr =
        "select scene_type, id, alive_time, interval, init_delay, callback_fitid, start_time, "
        "last_heartbeat_time, expired_time, status, host, port, protocol, formats, "
        "environment from registry_heartbeat";
    Fit::vector<const char*> sqlParams{};
    Fit::vector<AddressStatusInfo> resultCollector;
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

    resultCollector = table->query_all_beat();
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (const auto& result : resultCollector) {
        EXPECT_THAT(result, IsEqualStatusInfo(dummyStatusInfo));
    }
}
