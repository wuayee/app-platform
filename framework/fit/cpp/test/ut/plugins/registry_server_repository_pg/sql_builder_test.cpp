/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq sql builder test
 * Author       : x00649642
 * Date         : 2023/11/28
 */

#include "fit/stl/map.hpp"
#include "fit/stl/memory.hpp"
#include "fit/internal/util/json_converter_util.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/sql_wrapper/sql_builder.hpp"
#include "registry_server_repository_pg/src/table_service/utils/field_declare_helper.hpp"

using namespace ::testing;
using namespace Fit;

namespace {
enum class TestEntityEnum { ZERO, ONE, TWO };

struct TestEntity {
    int intMember;
    string strMember;
    map<string, string> jsonMember;
    TestEntityEnum enumMember;
};

using SqlBuilderType = Fit::Pg::SqlBuilder<TestEntity>;
using ColumnDescType = SqlBuilderType::ColumnDescT;

ColumnDescType intMemberDesc = PQ_HELPER_TYPE_INT_DECLARE("intMember", TestEntity, intMember);
ColumnDescType strMemberDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE("strMember", TestEntity, strMember);
ColumnDescType jsonMemberDesc = PQ_HELPER_JSON_FIELD_DECLARE("jsonMember", TestEntity, jsonMember);
ColumnDescType enumMemberDesc{"enumMember", Fit::Pg::TYPE_INT,
                              [](const TestEntity& object, Fit::vector<Fit::string>& holder) {
                                  holder.emplace_back(Fit::to_string(static_cast<int32_t>(object.enumMember)));
                                  return holder.back().c_str();
                              },
                              [](Fit::string value, TestEntity& object) {
                                  object.enumMember = static_cast<TestEntityEnum>(Fit::StringUtils::ToInt32(value));
                              }};

constexpr const char* table_name = "table_name";
constexpr int intMember = 1008;
constexpr const char* intMemberStr = "1008";
constexpr const char* strMember = "random string";
const map<string, string> jsonMember = {{"key", "random value"}, {"random key", "value"}};
constexpr const char* jsonMemberStr = "{\"key\":\"random value\",\"random key\":\"value\"}";
const int enumMember = 2;
constexpr const char* enumMemberStr = "2";
TestEntity entity{intMember, strMember, jsonMember, static_cast<TestEntityEnum>(enumMember)};
Fit::vector<ColumnDescType> GetAllColumns()
{
    return {intMemberDesc, strMemberDesc, jsonMemberDesc, enumMemberDesc};
}

class TestAndExpectSqlCmd {
    using SqlCmd = Fit::Pg::SqlCmd;

public:
    SqlCmd testCmd;
    SqlCmd expectCmd;
};
} // namespace

class SqlBuilderTest : public ::testing::TestWithParam<TestAndExpectSqlCmd> {};

TestAndExpectSqlCmd insertTestData[] = {
    {SqlBuilderType::BuildInsert(table_name, GetAllColumns(), entity),
     {"insert into table_name(intMember,strMember,jsonMember,enumMember) "
      "values ($1::int,$2::varchar,$3::varchar,$4::int)",
      {intMemberStr, strMember, jsonMemberStr, enumMemberStr},
      {}}}};

TestAndExpectSqlCmd updateTestData[] = {
    {SqlBuilderType::BuildUpdate(table_name, GetAllColumns(), {}, entity),
        {"update table_name "
         "set intMember=$1::int,strMember=$2::varchar,jsonMember=$3::varchar,enumMember=$4::int where ",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr},
         {}}},
    {SqlBuilderType::BuildUpdate(table_name, GetAllColumns(), {jsonMemberDesc, enumMemberDesc}, entity),
        {"update table_name "
         "set intMember=$1::int,strMember=$2::varchar,jsonMember=$3::varchar,enumMember=$4::int "
         "where jsonMember=$5::varchar and enumMember=$6::int",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr, jsonMemberStr, enumMemberStr},
         {}}}};

TestAndExpectSqlCmd deleteTestData[] = {
    {SqlBuilderType::BuildDelete(table_name, {jsonMemberDesc}, entity),
        {"delete from table_name where jsonMember=$1::varchar", {jsonMemberStr}, {}}},
    {SqlBuilderType::BuildDelete(table_name, {enumMemberDesc}, entity),
        {"delete from table_name where enumMember=$1::int", {enumMemberStr}, {}}},
    {SqlBuilderType::BuildDelete(table_name, GetAllColumns(), entity),
        {"delete from table_name where "
         "intMember=$1::int and strMember=$2::varchar and "
         "jsonMember=$3::varchar and enumMember=$4::int",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr}, {}}}};

TestAndExpectSqlCmd selectTestData[] = {
    {SqlBuilderType::BuildSelect(table_name, GetAllColumns(), {jsonMemberDesc}, entity),
        {"select intMember, strMember, jsonMember, enumMember from table_name where "
         "jsonMember=$1::varchar", {jsonMemberStr}, {}}},
    {SqlBuilderType::BuildSelect(table_name, {intMemberDesc}, {enumMemberDesc}, entity),
        {"select intMember from table_name where "
         "enumMember=$1::int", {enumMemberStr}, {}}},
    {SqlBuilderType::BuildSelect(table_name, GetAllColumns(), GetAllColumns(), entity),
        {"select intMember, strMember, jsonMember, enumMember from table_name where "
         "intMember=$1::int and strMember=$2::varchar and "
         "jsonMember=$3::varchar and enumMember=$4::int",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr}, {}}}};

TestAndExpectSqlCmd insertOrUpdateTestData[] = {
    {SqlBuilderType::BuildInsertOrUpdate(table_name, GetAllColumns(), "", {jsonMemberDesc}, entity),
        {"insert into table_name(intMember,strMember,jsonMember,enumMember) "
         "values ($1::int,$2::varchar,$3::varchar,$4::int)",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr}, {}}},
    {SqlBuilderType::BuildInsertOrUpdate(table_name, GetAllColumns(), "index", {}, entity),
        {"insert into table_name(intMember,strMember,jsonMember,enumMember) "
         "values ($1::int,$2::varchar,$3::varchar,$4::int) "
         "on conflict on constraint index do nothing",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr}, {}}},
    {SqlBuilderType::BuildInsertOrUpdate(table_name, GetAllColumns(), "index", GetAllColumns(), entity),
        {"insert into table_name(intMember,strMember,jsonMember,enumMember) "
         "values ($1::int,$2::varchar,$3::varchar,$4::int) "
         "on conflict on constraint index do update "
         "set intMember=$5::int,strMember=$6::varchar,jsonMember=$7::varchar,enumMember=$8::int",
         {intMemberStr, strMember, jsonMemberStr, enumMemberStr,
          intMemberStr, strMember, jsonMemberStr, enumMemberStr}, {}}}};

TEST_P(SqlBuilderTest, AssertSqlValid)
{
    // when
    auto sqlCmds = GetParam();
    // then
    EXPECT_EQ(sqlCmds.testCmd.sql, sqlCmds.expectCmd.sql);
    EXPECT_EQ(sqlCmds.testCmd.params.size(), sqlCmds.expectCmd.params.size());
    for (size_t i = 0; i < sqlCmds.expectCmd.params.size(); ++i) {
        EXPECT_STRCASEEQ(sqlCmds.testCmd.params[i], sqlCmds.expectCmd.params[i]);
    }
}

INSTANTIATE_TEST_CASE_P(InsertTest, SqlBuilderTest, testing::ValuesIn(insertTestData));
INSTANTIATE_TEST_CASE_P(UpdateTest, SqlBuilderTest, testing::ValuesIn(updateTestData));
INSTANTIATE_TEST_CASE_P(DeleteTest, SqlBuilderTest, testing::ValuesIn(deleteTestData));
INSTANTIATE_TEST_CASE_P(SelectTest, SqlBuilderTest, testing::ValuesIn(selectTestData));
INSTANTIATE_TEST_CASE_P(InsertOrUpdateTest, SqlBuilderTest, testing::ValuesIn(insertOrUpdateTestData));
