/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: test for resource_manager/FtokArgsGenerator
 */

#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include "util/DataBusUtil.h"
#include "fbs/apply_memory_message_response_generated.h"

using namespace DataBus::Common;
using ::testing::MockFunction;
using ::testing::_;

namespace DataBus {
namespace Test {
class DataBusUtilTest : public testing::Test {
protected:
    MockFunction<void(const uint8_t*, size_t)> mockSender;
};

TEST_F(DataBusUtilTest, should_send_message_when_size_correct)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    int32_t memoryId = 0;
    uint64_t memorySize = 100;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
            Common::CreateApplyMemoryMessageResponse(bodyBuilder, ErrorType::None, memoryId, memorySize);
    bodyBuilder.Finish(respBody);
    EXPECT_CALL(mockSender, Call(_, MESSAGE_HEADER_LEN + bodyBuilder.GetSize())).Times(1);
    DataBusUtil::SendMessage(bodyBuilder, MessageType::ApplyMemory, mockSender.AsStdFunction());
}

TEST_F(DataBusUtilTest, should_early_return_when_size_incorrect)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    EXPECT_CALL(mockSender, Call(_, _)).Times(0);
    // 在没有构建消息体的情况下直接构建消息头，导致消息尺寸错误
    DataBusUtil::SendMessage(bodyBuilder, MessageType::ApplyMemory, mockSender.AsStdFunction());
}

TEST_F(DataBusUtilTest, should_send_error_message_when_sender_given)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<ErrorMessageResponse> respBody =
            CreateErrorMessageResponse(bodyBuilder, ErrorType::IllegalMessageHeader);
    bodyBuilder.Finish(respBody);
    EXPECT_CALL(mockSender, Call(_, MESSAGE_HEADER_LEN + bodyBuilder.GetSize())).Times(1);
    DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, mockSender.AsStdFunction());
}
} // namespace Test
} // namespace DataBus