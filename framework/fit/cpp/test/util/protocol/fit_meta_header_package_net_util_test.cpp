/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 16:56
 * Notes:       :
 */

#include "util/protocol/fit_meta_header_package_net_util.h"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

namespace {
bool is_big_endian()
{
    uint32_t test = 1;
    return *((char *) &test) != 1;
}
}
class fit_meta_header_package_net_util_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_meta_header_package_net_util_test, should_return_net_order_when_host_to_net_given_header)
{
    // given
    fit_meta_header_package package {};
    package.version = 1;
    package.fit_id_length = 1;
    uint16_t expected_version = is_big_endian() ? 0x0001 : 0x0100;
    uint16_t expected_fit_id_length = is_big_endian() ? 0x0001 : 0x0100;

    // when
    fit_meta_header_package_net_util::host_to_net(package);

    // then
    EXPECT_THAT(package.version, ::testing::Eq(expected_version));
    EXPECT_THAT(package.fit_id_length, ::testing::Eq(expected_fit_id_length));
}

TEST_F(fit_meta_header_package_net_util_test, should_return_net_order_when_net_to_nhost_given_header)
{
    // given
    fit_meta_header_package package {};
    package.version = 1;
    package.fit_id_length = 1;
    uint16_t expected_version = is_big_endian() ? 0x0001 : 0x0100;
    uint16_t expected_fit_id_length = is_big_endian() ? 0x0001 : 0x0100;

    // when
    fit_meta_header_package_net_util::host_to_net(package);

    // then
    EXPECT_THAT(package.version, ::testing::Eq(expected_version));
    EXPECT_THAT(package.fit_id_length, ::testing::Eq(expected_fit_id_length));
}