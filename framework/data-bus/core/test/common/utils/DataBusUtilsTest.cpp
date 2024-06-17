/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: test for common/utils/DataBusUtils
 */

#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include "utils/DataBusUtils.h"
#include "fbs/apply_memory_message_response_generated.h"

using namespace DataBus::Common;
using namespace DataBus::Common::Utils;
using ::testing::MockFunction;
using ::testing::_;

namespace DataBus {
namespace Test {
class DataBusUtilsTest : public testing::Test {
protected:
    MockFunction<void(const uint8_t*, size_t)> mockSender;
};

TEST_F(DataBusUtilsTest, should_send_message_when_size_correct)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    int32_t memoryId = 0;
    uint64_t memorySize = 100;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
            Common::CreateApplyMemoryMessageResponse(bodyBuilder, ErrorType::None, memoryId, memorySize);
    bodyBuilder.Finish(respBody);
    EXPECT_CALL(mockSender, Call(_, MESSAGE_HEADER_LEN + bodyBuilder.GetSize())).Times(1);
    Utils::SendMessage(bodyBuilder, MessageType::ApplyMemory, mockSender.AsStdFunction());
}

TEST_F(DataBusUtilsTest, should_send_error_message_when_sender_given)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<ErrorMessageResponse> respBody =
            CreateErrorMessageResponse(bodyBuilder, ErrorType::IllegalMessageHeader);
    bodyBuilder.Finish(respBody);
    EXPECT_CALL(mockSender, Call(_, MESSAGE_HEADER_LEN + bodyBuilder.GetSize())).Times(1);
    Utils::SendErrorMessage(ErrorType::IllegalMessageHeader, mockSender.AsStdFunction());
}
} // namespace Test
} // namespace DataBus