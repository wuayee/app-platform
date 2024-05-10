/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/28 11:29
 */
#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include <fit/internal/util/protocol/tlv/varying_number.hpp>

class VaryingNumberTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(VaryingNumberTest, should_return_success_when_TransToBytes_given_data)
{
    uint32_t data = 127;
    auto bytesData = Fit::VaryNumber::TransToBytes(data);
    auto intValue = Fit::VaryNumber::BytesToInt(bytesData);
    std::cout << data << " " << intValue << std::endl;
    EXPECT_THAT(data, ::testing::Eq(intValue));

    data = 32767;
    bytesData = Fit::VaryNumber::TransToBytes(data);
    intValue = Fit::VaryNumber::BytesToInt(bytesData);
    EXPECT_THAT(data, ::testing::Eq(intValue));

    data = 8388607;
    bytesData = Fit::VaryNumber::TransToBytes(data);
    intValue = Fit::VaryNumber::BytesToInt(bytesData);
    EXPECT_THAT(data, ::testing::Eq(intValue));

    data = 4294936296;
    bytesData = Fit::VaryNumber::TransToBytes(data);
    intValue = Fit::VaryNumber::BytesToInt(bytesData);
    EXPECT_THAT(data, ::testing::Eq(intValue));
}

TEST_F(VaryingNumberTest, should_return_success_when_BytesToInt_given_one_varyingNumber_data_and_other_data)
{
    uint32_t data = 127;
    auto bytesData = Fit::VaryNumber::TransToBytes(data);
    bytesData.append("12341238oiisajhfnaskl;hfdj;alsdfhnal;sddf");

    auto intValue = Fit::VaryNumber::BytesToInt(bytesData);
    EXPECT_THAT(data, ::testing::Eq(intValue));
}

TEST_F(VaryingNumberTest, should_return_success_when_BytesToInt_given_two_varyingNumber_data_and_other_data)
{
    uint32_t data = 127;
    uint32_t dataTwo = 1000000000;
    auto bytesData = Fit::VaryNumber::TransToBytes(data);
    bytesData.append(Fit::VaryNumber::TransToBytes(dataTwo));
    bytesData.append("12341238oiisajhfnaskl;hfdj;alsdfhnal;sddf");

    uint32_t expectData;
    uint32_t expectDataTwo;
    auto dataSize = (uint32_t)bytesData.size();

    auto dataStream = Fit::VaryNumber::BytesToInt(bytesData.c_str(), dataSize, expectData);

    dataStream = Fit::VaryNumber::BytesToInt(dataStream, dataSize, expectDataTwo);

    std::cout << data << " " << expectData << std::endl;
    std::cout << dataTwo << " " << expectDataTwo << std::endl;

    EXPECT_THAT(data, ::testing::Eq(expectData));
    EXPECT_THAT(dataTwo, ::testing::Eq(expectDataTwo));
}
