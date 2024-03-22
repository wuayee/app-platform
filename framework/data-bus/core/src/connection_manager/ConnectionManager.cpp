/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/
#include "ConnectionManager.h"

#include <iostream>
#include <tuple>
#include <algorithm>

#include "flatbuffers/flatbuffers.h"

#include "fbs/apply_memory_message_generated.h"
#include "fbs/apply_memory_message_response_generated.h"
#include "fbs/common_generated.h"
#include "stl/memory.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Connection {

constexpr int32_t MESSAGE_HEADER_LEN = 24;

void ConnectionManager::AddNewConnection(int socketFd)
{
    unique_ptr<Connection> connection(new Connection(socketFd));
    connections_[socketFd] = std::move(connection);
}


void ConnectionManager::Handle(const char buffer[], ssize_t len, int socketFd)
{
    if (len < MESSAGE_HEADER_LEN) {
        return;
    }

    // 验证buf是否包含有效的消息头
    flatbuffers::Verifier verifier(reinterpret_cast<const uint8_t*>(buffer), MESSAGE_HEADER_LEN);
    if (!Common::VerifyMessageHeaderBuffer(verifier)) {
        cout << "verification header failure." << endl;
        return;
    }

    const Common::MessageHeader* header = Common::GetMessageHeader(buffer);

    // 读取消息体的大小和类型
    switch (header->type()) {
        case Common::MessageType::HeartBeat: {
            cout << "received heartbeat" << endl;
            break;
        }
        case Common::MessageType::ApplyMemory: {
            HandleMessageApplyMemory(header, buffer, len, socketFd);
            break;
        }
        default:
            cout << "unknown message" << endl;
            break;
    }
}


void ConnectionManager::HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, ssize_t len,
                                                 int socketFd)
{
    // TODO: 需要处理半包和粘包
    uint bodySize = header->size();
    if (len < bodySize + MESSAGE_HEADER_LEN) {
        cout << "Message size not enough" << endl;
        return;
    }

    // 解析消息体
    auto startPtr = buffer + bodySize;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), bodySize);
    if (!Common::VerifyMessageHeaderBuffer(bodyVerifier)) {
        cout << "verification ApplyMemory body failure." << endl;
        return;
    }
    const Common::ApplyMemoryMessage* applyMemoryMessage =
            Common::GetApplyMemoryMessage(startPtr);
    cout << "received ApplyMemory, size: " << applyMemoryMessage->memory_size() << endl;

    // TODO: 在这里加入申请内存调用

    // 生成返回信息
    flatbuffers::FlatBufferBuilder bodyBuilder;
    int fakeRes = 100;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
            Common::CreateApplyMemoryMessageResponse(bodyBuilder, Common::ErrorType::None, fakeRes);
    bodyBuilder.Finish(respBody);

    uint8_t* resBodyBuf = bodyBuilder.GetBufferPointer();
    size_t respBodySize = bodyBuilder.GetSize();

    flatbuffers::FlatBufferBuilder headerBuilder;
    flatbuffers::Offset<Common::MessageHeader> respHeader =
            Common::CreateMessageHeader(headerBuilder, Common::MessageType::ApplyMemory, respBodySize);
    headerBuilder.Finish(respHeader);

    uint8_t* headerBodyBuf = headerBuilder.GetBufferPointer();
    size_t respHeaderSize = headerBuilder.GetSize();

    // 组合消息头和消息体
    const size_t respSize = respHeaderSize + respBodySize;
    auto message = DataBus::make_unique<uint8_t[]>(respSize);
    std::copy(headerBodyBuf, headerBodyBuf + respHeaderSize, message.get());
    std::copy(resBodyBuf, resBodyBuf + respBodySize, message.get() + respHeaderSize);

    cout << "Reply ApplyMemory, header: " << respHeaderSize << ", body: " << respBodySize << endl;
    connections_[socketFd]->Send(message.get(), respSize);
}
}  // namespace Connection
}  // namespace DataBus
