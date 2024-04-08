/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/
#include "ConnectionManager.h"

#include <algorithm>
#include <iostream>
#include <memory>
#include <sstream>
#include <tuple>

#include "flatbuffers/flatbuffers.h"

#include "exception/databus_exception.h"
#include "fbs/apply_memory_message_generated.h"
#include "fbs/apply_memory_message_response_generated.h"
#include "fbs/apply_permission_message_generated.h"
#include "fbs/apply_permission_message_response_generated.h"
#include "fbs/common_generated.h"
#include "ResourceManager.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Connection {

constexpr int32_t MESSAGE_HEADER_LEN = 24;

/**
 * 生成消息头，拼接整个消息并发送
 */
std::unique_ptr<uint8_t[]> WrapWithHeader(const flatbuffers::FlatBufferBuilder& bodyBuilder, Common::MessageType type)
{
    uint8_t* resBodyBuf = bodyBuilder.GetBufferPointer();
    size_t respBodySize = bodyBuilder.GetSize();
    flatbuffers::FlatBufferBuilder headerBuilder;
    flatbuffers::Offset<Common::MessageHeader> respHeader =
        Common::CreateMessageHeader(headerBuilder, type, respBodySize);
    headerBuilder.Finish(respHeader);

    uint8_t* headerBodyBuf = headerBuilder.GetBufferPointer();

    if (headerBuilder.GetSize() != MESSAGE_HEADER_LEN) {
        // TD: format util
        std::stringstream ss;
        ss << "Incorrect header size, expected: " << MESSAGE_HEADER_LEN
           << ", actual: " << headerBuilder.GetSize() << endl;
        throw IllegalMessageHeaderException(ss.str());
    }
    size_t respHeaderSize = headerBuilder.GetSize();

    // 组合消息头和消息体
    const size_t respSize = respHeaderSize + respBodySize;
    auto message = std::make_unique<uint8_t[]>(respSize);
    std::copy(headerBodyBuf, headerBodyBuf + respHeaderSize, message.get());
    std::copy(resBodyBuf, resBodyBuf + respBodySize, message.get() + respHeaderSize);

    return message;
}


void ConnectionManager::AddNewConnection(int socketFd)
{
    unique_ptr<Connection> connection(new Connection(socketFd));
    connections_[socketFd] = std::move(connection);
}


void ConnectionManager::Handle(const char buffer[], ssize_t len, int socketFd,
                               const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    if (len < MESSAGE_HEADER_LEN) {
        return;
    }

    // 验证buf是否包含有效的消息头
    flatbuffers::Verifier verifier(reinterpret_cast<const uint8_t*>(buffer), MESSAGE_HEADER_LEN);
    if (!Common::VerifyMessageHeaderBuffer(verifier)) {
        throw IllegalMessageHeaderException("received incorrect header format");
    }

    const Common::MessageHeader* header = Common::GetMessageHeader(buffer);

    // TODO: 需要处理半包和粘包
    uint bodySize = header->size();
    if (len < bodySize + MESSAGE_HEADER_LEN) {
        throw IllegalMessageBodyException("received incorrect body format");
    }

    // 读取消息体的大小和类型
    switch (header->type()) {
        case Common::MessageType::HeartBeat: {
            cout << "received heartbeat" << endl;
            break;
        }
        case Common::MessageType::ApplyMemory: {
            HandleMessageApplyMemory(header, buffer, socketFd, resourceMgrPtr);
            break;
        }
        case Common::MessageType::ApplyPermission: {
            HandleMessageApplyPermission(header, buffer, socketFd, resourceMgrPtr);
            break;
        }
        default:
            throw IllegalMessageBodyException("unknown message body type");
            break;
    }
}


void ConnectionManager::HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd,
                                                 const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    // 解析消息体
    auto startPtr = buffer + header->size();
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyMessageHeaderBuffer(bodyVerifier)) {
        throw IllegalMessageBodyException("received incorrect apply memory body format");
    }
    const Common::ApplyMemoryMessage* applyMemoryMessage =
            Common::GetApplyMemoryMessage(startPtr);
    cout << "received ApplyMemory, size: " << applyMemoryMessage->memory_size() << endl;

    int memoryId = resourceMgrPtr->HandleApplyMemory(socketFd, applyMemoryMessage->memory_size());

    // 生成返回信息体
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
            Common::CreateApplyMemoryMessageResponse(bodyBuilder, Common::ErrorType::None,
                                                     memoryId, applyMemoryMessage->memory_size());
    bodyBuilder.Finish(respBody);

    auto message = WrapWithHeader(bodyBuilder, Common::MessageType::ApplyMemory);

    connections_[socketFd]->Send(message.get(), bodyBuilder.GetSize() + MESSAGE_HEADER_LEN);
}

void ConnectionManager::HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer,
                                                     int socketFd,
                                                     const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    // 解析消息体
    auto startPtr = buffer + header->size();
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyApplyPermissionMessageBuffer(bodyVerifier)) {
        throw IllegalMessageBodyException("received incorrect apply permission body format");
    }
    const Common::ApplyPermissionMessage* applyPermissionMessage =
        Common::GetApplyPermissionMessage(startPtr);

    cout << "received ApplyMemory, permission: " << static_cast<int8_t>(applyPermissionMessage->permission()) << endl;

    // TD: 在这里加入申请权限调用

    // 生成返回信息体
    flatbuffers::FlatBufferBuilder bodyBuilder;
    int fakeRes = 100;
    flatbuffers::Offset<Common::ApplyPermissionMessageResponse> respBody =
        Common::CreateApplyPermissionMessageResponse(bodyBuilder, Common::ErrorType::None, true, fakeRes);
    bodyBuilder.Finish(respBody);

    auto message = WrapWithHeader(bodyBuilder, Common::MessageType::ApplyPermission);

    connections_[socketFd]->Send(message.get(), bodyBuilder.GetSize() + MESSAGE_HEADER_LEN);
}
}  // namespace Connection
}  // namespace DataBus
