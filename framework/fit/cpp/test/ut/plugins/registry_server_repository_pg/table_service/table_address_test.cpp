/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table address test
 * Author       : x00649642
 * Date         : 2023/12/01
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/registry_server/table_address.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Repository::Pg;
using Address = Fit::RegistryInfo::Address;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* HOST = "hostV";
constexpr auto PORT = 10086;
constexpr const char* PORT_STR = "10086";
constexpr auto PROTOCOL = Fit::fit_protocol_type::HTTP;
constexpr const char* PROTOCOL_STR = "2";
constexpr const char* WORKER_ID = "workerIdV";
}  // namespace

class TableAddressTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableAddress>();
        dummyAddress.host = HOST;
        dummyAddress.port = PORT;
        dummyAddress.protocol = PROTOCOL;
        dummyAddress.workerId = WORKER_ID;
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
    Fit::unique_ptr<TableAddress> table;
    Address dummyAddress;
};

TEST_F(TableAddressTest, should_success_when_init)
{
    EXPECT_TRUE(table->Init());
}

TEST_F(TableAddressTest, should_success_when_save_0_address)
{
    auto connectionCreator = [this](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();

        using namespace ::testing;
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);
    EXPECT_EQ(table->Save(Fit::vector<Fit::RegistryInfo::Address>()), FIT_ERR_SUCCESS);
}

TEST_F(TableAddressTest, should_success_when_save_1_address_success)
{
    constexpr const char* sqlCmdStr =
        "insert into registry_address(host,port,protocol,worker_id) values ($1::varchar,$2::int,$3::int,$4::varchar) "
        "on conflict on constraint registry_address_index do update set protocol=$5::int";
    Fit::vector<const char*> sqlParams{HOST, PORT_STR, PROTOCOL_STR, WORKER_ID, PROTOCOL_STR};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(2).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(1).WillOnce(Return(1));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    EXPECT_EQ(table->Save(dummyAddress), FIT_ERR_SUCCESS);
}

TEST_F(TableAddressTest, should_success_when_save_address_vector_success)
{
    constexpr const char* sqlCmdStr =
        "insert into registry_address(host,port,protocol,worker_id) values ($1::varchar,$2::int,$3::int,$4::varchar) "
        "on conflict on constraint registry_address_index do update set protocol=$5::int";
    Fit::vector<const char*> sqlParams{HOST, PORT_STR, PROTOCOL_STR, WORKER_ID, PROTOCOL_STR};
    Fit::vector<Address> dummyAddresses(3, dummyAddress);
    size_t count = 0;
    auto sqlExecResultGenerator = [this, &count, dummyAddresses]() {
        ++count;
        // the last result will be called twice on isOk for now
        auto isOkCallTime = (count == dummyAddresses.size()) ? 2 : 1;
        using namespace ::testing;
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(isOkCallTime).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(1).WillOnce(Return(1));
        return sqlExecResultMock;
    };
    auto connectionCreator = [this, sqlCmdStr, sqlParams, sqlExecResultGenerator, dummyAddresses](const char*) {
        auto connectionMock = Fit::make_unique<SqlConnectionMock>();

        using namespace ::testing;
        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(dummyAddresses.size())
            .WillRepeatedly(sqlExecResultGenerator);
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    EXPECT_EQ(table->Save(dummyAddresses), FIT_ERR_SUCCESS);
}

TEST_F(TableAddressTest, should_success_when_delete_by_worker_id_success)
{
    constexpr const char* sqlCmdStr = "delete from registry_address where worker_id=$1::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    EXPECT_EQ(table->Delete(WORKER_ID), FIT_ERR_SUCCESS);
}

TEST_F(TableAddressTest, should_success_when_select_by_worker_id_success)
{
    constexpr const char* sqlCmdStr =
        "select host, port, protocol, worker_id from registry_address where worker_id=$1::varchar";
    Fit::vector<const char*> sqlParams{WORKER_ID};
    Fit::vector<Fit::string> sqlResultStr{HOST, PORT_STR, PROTOCOL_STR, WORKER_ID};
    Fit::vector<Address> addressesResult;
    auto connectionCreator = [this, sqlCmdStr, sqlParams, sqlResultStr](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(1));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(sqlResultStr.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(1).WillOnce(Return(sqlResultStr));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->Query(WORKER_ID, addressesResult), FIT_ERR_SUCCESS);
    ASSERT_EQ(addressesResult.size(), 1);
    EXPECT_EQ(addressesResult[0].host, HOST);
    EXPECT_EQ(addressesResult[0].port, PORT);
    EXPECT_EQ(addressesResult[0].protocol, PROTOCOL);
    EXPECT_EQ(addressesResult[0].workerId, WORKER_ID);
}

TEST_F(TableAddressTest, should_success_when_select_all_success)
{
    constexpr const char* sqlCmdStr = "select host, port, protocol, worker_id from registry_address";
    Fit::vector<Fit::string> sqlResultStr{HOST, PORT_STR, PROTOCOL_STR, WORKER_ID};
    Fit::vector<Address> addressesResult;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr, sqlResultStr](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(sqlResultStr.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(sqlResultStr));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), IsEmpty()))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->QueryAll(addressesResult), FIT_ERR_SUCCESS);
    ASSERT_EQ(addressesResult.size(), resultCount);
    for (size_t idx = 0; idx < resultCount; ++idx) {
        EXPECT_EQ(addressesResult[idx].host, HOST);
        EXPECT_EQ(addressesResult[idx].port, PORT);
        EXPECT_EQ(addressesResult[idx].protocol, PROTOCOL);
        EXPECT_EQ(addressesResult[idx].workerId, WORKER_ID);
    }
}
