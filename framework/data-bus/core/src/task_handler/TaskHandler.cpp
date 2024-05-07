/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description : Provides implementation for databus task process center
*/

#include "TaskHandler.h"

#include <tuple>

#include "flatbuffers/flatbuffers.h"

#include "util/DataBusUtil.h"
#include "fbs/apply_memory_message_generated.h"
#include "fbs/apply_memory_message_response_generated.h"
#include "fbs/apply_permission_message_generated.h"
#include "fbs/apply_permission_message_response_generated.h"
#include "fbs/release_permission_message_generated.h"
#include "log/Logger.h"

using namespace std;
using namespace DataBus::Task;
using namespace DataBus::Common;

namespace DataBus {
namespace Task {

void TaskHandler::Init()
{
    thread_ = std::thread(&TaskHandler::Loop, this);
}

[[noreturn]] void TaskHandler::Loop()
{
    while (true) {
        std::unique_ptr<Task> task = taskLoopPtr_->GetNextTask();
        // 读取消息体的大小和类型
        switch (task->Type()) {
            case Task::TaskType::OPEN: {
                connectionMgrPtr_->AddNewConnection(task->ClientFd());
                DataBus::logger.Info("Client {} connected", task->ClientFd());
                break;
            }
            case Task::TaskType::READ: {
                HandleRead(*task);
                break;
            }
            case Task::TaskType::WRITE: {
                HandleWrite(*task);
                break;
            }
            case Task::TaskType::CLOSE: {
                connectionMgrPtr_->CloseConnection(task->ClientFd());
                break;
            }
            default:
                break;
        }
    }
}

void TaskHandler::HandleWrite(const Task& task)
{
    ErrorType type = connectionMgrPtr_->Send(task.ClientFd(), task.DataRaw(), task.Size());
    // 写入失败则关闭连接
    if (type != ErrorType::None) {
        taskLoopPtr_->AddCloseTask(task.ClientFd());
    }
}

void TaskHandler::HandleRead(const Task& task)
{
    const size_t len = task.Size();
    const char* buffer = task.DataRaw();
    const int socketFd = task.ClientFd();

    if (len < MESSAGE_HEADER_LEN) {
        logger.Error("[TaskHandler] Incorrect message header length");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    // 验证buf是否包含有效的消息头
    flatbuffers::Verifier verifier(reinterpret_cast<const uint8_t*>(buffer), MESSAGE_HEADER_LEN);
    if (!Common::VerifyMessageHeaderBuffer(verifier)) {
        logger.Error("[TaskHandler] Incorrect message header format");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    const Common::MessageHeader* header = Common::GetMessageHeader(buffer);

    // TODO: 需要处理半包和粘包
    uint bodySize = header->size();
    if (len < bodySize + MESSAGE_HEADER_LEN) {
        logger.Error("[TaskHandler] Incorrect message body length");
        DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageBody, GetSender(socketFd));
        return;
    }

    // 读取消息体的大小和类型
    switch (header->type()) {
        case Common::MessageType::HeartBeat: {
            logger.Info("[TaskHandler] Received heartbeat");
            break;
        }
        case Common::MessageType::RuntimeReport: {
            DataBus::Runtime::RuntimeReporter::Instance().Report();
            break;
        }
        case Common::MessageType::ApplyMemory: {
            HandleMessageApplyMemory(header, buffer, socketFd);
            break;
        }
        case Common::MessageType::ApplyPermission: {
            HandleMessageApplyPermission(header, buffer, socketFd);
            break;
        }
        case Common::MessageType::ReleasePermission: {
            HandleMessageReleasePermission(header, buffer, socketFd);
            break;
        }
        default:
            logger.Error("[TaskHandler] Unknown message type");
            DataBusUtil::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
    }
}

void TaskHandler::HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 解析消息体
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyMessageHeaderBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect apply memory body format");
        SendApplyMemoryResponse(socketFd, -1, 0, ErrorType::IllegalMessageBody);
    }
    const Common::ApplyMemoryMessage* applyMemoryMessage =
        Common::GetApplyMemoryMessage(startPtr);
    logger.Info("[TaskHandler] Received ApplyMemory, size: {}", applyMemoryMessage->memory_size());

    const tuple<int32_t, ErrorType> applyMemoryRes =
        resourceMgrPtr_->HandleApplyMemory(socketFd, applyMemoryMessage->memory_size());
    int32_t memoryId;
    ErrorType errorType;
    tie(memoryId, errorType) = applyMemoryRes;
    uint64_t memorySize = errorType == ErrorType::None ? applyMemoryMessage->memory_size() : 0;
    SendApplyMemoryResponse(socketFd, memoryId, memorySize, errorType);
}

void TaskHandler::HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 解析消息体
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyApplyPermissionMessageBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect apply permission body format");
        SendApplyPermissionResponse(socketFd, false, 0, ErrorType::IllegalMessageBody);
    }
    const Common::ApplyPermissionMessage* applyPermissionMessage =
        Common::GetApplyPermissionMessage(startPtr);

    logger.Info("[TaskHandler] Received ApplyPermission, permission: {}",
                static_cast<int8_t>(applyPermissionMessage->permission()));

    tuple<bool, uint64_t, ErrorType> applyPermitRes =
        resourceMgrPtr_->HandleApplyPermission(socketFd, applyPermissionMessage->permission(),
                                               applyPermissionMessage->memory_key());
    bool granted;
    uint64_t memorySize;
    ErrorType errorType;
    tie(granted, memorySize, errorType) = applyPermitRes;
    // 权限申请请求进入等待队列，阻塞客户端通知
    if (!granted && errorType == ErrorType::None) {
        return;
    }
    SendApplyPermissionResponse(socketFd, granted, memorySize, errorType);
}

void TaskHandler::HandleMessageReleasePermission(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 解析消息体
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyReleasePermissionMessageBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect release permission body format");
    }
    const Common::ReleasePermissionMessage* releasePermissionMessage =
        Common::GetReleasePermissionMessage(startPtr);
    logger.Info("[TaskHandler] Received ReleasePermission, permission: {}",
                static_cast<int8_t>(releasePermissionMessage->permission()));
    if (!resourceMgrPtr_->HandleReleasePermission(socketFd, releasePermissionMessage->permission(),
                                                  releasePermissionMessage->memory_key())) {
        logger.Error("[TaskHandler] Failed to ReleasePermission, client: {}, permission: {}", socketFd,
                     static_cast<int8_t>(releasePermissionMessage->permission()));
        return;
    }
    // 通知结束等待的客户端权限申请成功
    vector<tuple<int32_t, uint64_t>> notificationQueue =
        resourceMgrPtr_->ProcessWaitingPermitRequests(releasePermissionMessage->memory_key());
    for (const auto& notification: notificationQueue) {
        int32_t clientId;
        uint64_t memorySize;
        tie(clientId, memorySize) = notification;
        SendApplyPermissionResponse(clientId, true, memorySize, ErrorType::None);
    }
}

void TaskHandler::SendApplyMemoryResponse(int32_t socketFd, int32_t memoryId, uint64_t memorySize,
                                          ErrorType errorType)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<Common::ApplyMemoryMessageResponse> respBody =
        Common::CreateApplyMemoryMessageResponse(bodyBuilder, errorType, memoryId,
                                                 memorySize);
    bodyBuilder.Finish(respBody);
    DataBusUtil::SendMessage(bodyBuilder, Common::MessageType::ApplyMemory, GetSender(socketFd));
}

void TaskHandler::SendApplyPermissionResponse(int32_t socketFd, bool granted, uint64_t memorySize,
                                              ErrorType errorType)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<Common::ApplyPermissionMessageResponse> respBody =
        Common::CreateApplyPermissionMessageResponse(bodyBuilder, errorType, granted, memorySize);
    bodyBuilder.Finish(respBody);
    DataBusUtil::SendMessage(bodyBuilder, Common::MessageType::ApplyPermission, GetSender(socketFd));
}

std::function<void(const uint8_t*, size_t)> TaskHandler::GetSender(int32_t socketFd)
{
    return [this, socketFd](const uint8_t* buf, ssize_t s) {
        taskLoopPtr_->AddWriteTask(socketFd, reinterpret_cast<const char*>(buf), s);
    };
}
}  // Task
}  // DataBus
