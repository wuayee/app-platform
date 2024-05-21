/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: Utility method set for DataBus Core.
 */

#ifndef DATABUS_UTILS_H
#define DATABUS_UTILS_H

#include "flatbuffers/flatbuffers.h"
#include "fbs/message_header_generated.h"
#include "fbs/error_message_response_generated.h"
#include "log/Logger.h"

namespace DataBus {
namespace Common {
namespace Utils {

constexpr int32_t MESSAGE_HEADER_LEN = 24;

/**
 * 生成消息头，拼接整个消息并发送
 */
static void SendMessage(flatbuffers::FlatBufferBuilder &builder, Common::MessageType type,
                        const std::function<void(const uint8_t *, size_t)> &sender)
{
    const size_t respBodySize = builder.GetSize();
    FinishMessageHeaderBuffer(builder, Common::CreateMessageHeader(builder, type, respBodySize));
    if (builder.GetSize() != MESSAGE_HEADER_LEN + respBodySize) {
        logger.Error("Incorrect header size, expected: {}, actual: {}", MESSAGE_HEADER_LEN + respBodySize,
                     builder.GetSize());
        return;
    }
    sender(builder.GetBufferPointer(), builder.GetSize());
}

static void SendErrorMessage(ErrorType errorType, const std::function<void(const uint8_t *, size_t)> &sender)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<ErrorMessageResponse> respBody =
            CreateErrorMessageResponse(bodyBuilder, errorType);
    bodyBuilder.Finish(respBody);
    SendMessage(bodyBuilder, MessageType::Error, sender);
}
} // namespace Utils
} // namespace Common
} // namespace DataBus
#endif // DATABUS_UTILS_H
