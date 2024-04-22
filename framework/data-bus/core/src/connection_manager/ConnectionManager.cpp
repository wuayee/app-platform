/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/
#include "ConnectionManager.h"

#include <algorithm>
#include <memory>
#include <tuple>

#include "flatbuffers/flatbuffers.h"

#include "util/DataBusUtil.h"
#include "fbs/apply_memory_message_generated.h"
#include "fbs/apply_memory_message_response_generated.h"
#include "fbs/apply_permission_message_generated.h"
#include "fbs/apply_permission_message_response_generated.h"
#include "ResourceManager.h"
#include "log/Logger.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Connection {

void ConnectionManager::AddNewConnection(int socketFd)
{
    unique_ptr<Connection> connection(new Connection(socketFd));
    connections_[socketFd] = std::move(connection);
}


void ConnectionManager::Handle(const char buffer[], ssize_t len, int socketFd,
                               const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    if (len < MESSAGE_HEADER_LEN) {
        logger.Error("[ConnectionManager] Incorrect message header length");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    // 验证buf是否包含有效的消息头
    flatbuffers::Verifier verifier(reinterpret_cast<const uint8_t*>(buffer), MESSAGE_HEADER_LEN);
    if (!Common::VerifyMessageHeaderBuffer(verifier)) {
        logger.Error("[ConnectionManager] Incorrect message header format");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    const Common::MessageHeader* header = Common::GetMessageHeader(buffer);

    // TODO: 需要处理半包和粘包
    uint bodySize = header->size();
    if (len < bodySize + MESSAGE_HEADER_LEN) {
        logger.Error("[ConnectionManager] Incorrect message body length");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageBody, GetSender(socketFd));
        return;
    }

    // 读取消息体的大小和类型
    switch (header->type()) {
        case Common::MessageType::HeartBeat: {
            logger.Info("[ConnectionManager] Received heartbeat");
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
            logger.Error("[ConnectionManager] Unknown message type");
            DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
    }
}


void ConnectionManager::HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd,
                                                 const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    // 解析消息体
    auto startPtr = buffer + header->size();
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyMessageHeaderBuffer(bodyVerifier)) {
        logger.Error("[ConnectionManager] Received incorrect apply memory body format");
        SendApplyMemoryResponse(socketFd, -1, 0, ErrorType::IllegalMessageBody);
    }
    const Common::ApplyMemoryMessage* applyMemoryMessage =
            Common::GetApplyMemoryMessage(startPtr);
    logger.Info("[ConnectionManager] Received ApplyMemory, size: {}", applyMemoryMessage->memory_size());

    const tuple<int32_t, ErrorType> applyMemoryRes =
            resourceMgrPtr->HandleApplyMemory(socketFd, applyMemoryMessage->memory_size());
    uint64_t memorySize = get<1>(applyMemoryRes) == ErrorType::None ? applyMemoryMessage->memory_size() : 0;
    SendApplyMemoryResponse(socketFd, get<0>(applyMemoryRes), memorySize, get<1>(applyMemoryRes));
}

void ConnectionManager::HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer,
                                                     int socketFd,
                                                     const unique_ptr<Resource::ResourceManager>& resourceMgrPtr)
{
    // 解析消息体
    auto startPtr = buffer + header->size();
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyApplyPermissionMessageBuffer(bodyVerifier)) {
        logger.Error("[ConnectionManager] Received incorrect apply permission body format");
        SendApplyPermissionResponse(socketFd, false, 0, ErrorType::IllegalMessageBody);
    }
    const Common::ApplyPermissionMessage* applyPermissionMessage =
        Common::GetApplyPermissionMessage(startPtr);

    logger.Info("[ConnectionManager] Received ApplyMemory, permission: {}",
                static_cast<int8_t>(applyPermissionMessage->permission()));

    // TD: 在这里加入申请权限调用

    int fakeRes = 100;
    SendApplyPermissionResponse(socketFd, true, fakeRes, ErrorType::None);
}

void ConnectionManager::SendApplyMemoryResponse(int32_t socketFd, int32_t memoryId, uint64_t memorySize,
                                                ErrorType errorType)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
            Common::CreateApplyMemoryMessageResponse(bodyBuilder, errorType, memoryId,
                                                     memorySize);
    bodyBuilder.Finish(respBody);
    DataBusUtil::SendMessage(bodyBuilder, Common::MessageType::ApplyMemory, GetSender(socketFd));
}

void ConnectionManager::SendApplyPermissionResponse(int32_t socketFd, bool granted, uint64_t memorySize,
                                                    ErrorType errorType)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<Common::ApplyPermissionMessageResponse> respBody =
            Common::CreateApplyPermissionMessageResponse(bodyBuilder, errorType, granted, memorySize);
    bodyBuilder.Finish(respBody);
    DataBusUtil::SendMessage(bodyBuilder, Common::MessageType::ApplyPermission, GetSender(socketFd));
}

std::function<void(const uint8_t*, size_t)> ConnectionManager::GetSender(int32_t socketFd)
{
    return [this, socketFd](const uint8_t* buf, size_t s) {
        connections_[socketFd]->Send(buf, s);
    };
}
}  // namespace Connection
}  // namespace DataBus
