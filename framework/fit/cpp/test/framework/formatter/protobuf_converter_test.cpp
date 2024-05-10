/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/13
 * Notes:       :
 */

#include <fit/external/framework/formatter/protobuf_converter.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework::Formatter::Protobuf;

class ProtobufConverterTest : public testing::Test {
public:
    void SetUp() override
    {
        ctx = Fit::Context::NewContext();
    }

    void TearDown() override
    {
        ContextDestroy(ctx);
    }

    ContextObj ctx {};
};

TEST_F(ProtobufConverterTest,
    should_return_repeated_bool_when_call_DeserializeStringToRepeatedArg_given_repeated_bool_buffer)
{
    // given
    Fit::vector<bool> expected = {true, false};
    FitRepeatedArgument repeatedArgument;
    BooleanMessage b;
    b.set_value(true);
    repeatedArgument.add_arguments(b.SerializeAsString());
    b.set_value(false);
    repeatedArgument.add_arguments(b.SerializeAsString());

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Protobuf::DeserializeStringToRepeatedArg<Fit::vector<bool> **,
        Fit::vector<bool> *>(
        ctx, Fit::to_fit_string(repeatedArgument.SerializeAsString()), out);

    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        auto **res = Fit::any_cast<Fit::vector<bool> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(ProtobufConverterTest,
    should_return_repeated_bool_buffer_when_call_SerializeRepeatedArgToString_given_repeated_bool_value)
{
    // given
    Fit::Framework::Argument in = Fit::vector<bool> {true, false};
    FitRepeatedArgument repeatedArgument;
    BooleanMessage b;
    b.set_value(true);
    repeatedArgument.add_arguments(b.SerializeAsString());
    b.set_value(false);
    repeatedArgument.add_arguments(b.SerializeAsString());
    auto expectBuffer = repeatedArgument.SerializeAsString();

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Protobuf::SerializeRepeatedArgToString<Fit::vector<bool>, Fit::vector<bool>>(
        ctx, in, result);

    ASSERT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, Fit::to_fit_string(expectBuffer));
}
