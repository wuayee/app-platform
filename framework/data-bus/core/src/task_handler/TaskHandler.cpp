/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description : Provides implementation for databus task process center
*/

#include "TaskHandler.h"

#include <tuple>

#include "flatbuffers/flatbuffers.h"

#include "utils/DataBusUtils.h"
#include "fbs/apply_memory_message_generated.h"
#include "fbs/apply_memory_message_response_generated.h"
#include "fbs/apply_permission_message_generated.h"
#include "fbs/apply_permission_message_response_generated.h"
#include "fbs/get_meta_data_message_generated.h"
#include "fbs/get_meta_data_message_response_generated.h"
#include "fbs/release_permission_message_generated.h"
#include "log/Logger.h"
#include "fbs/release_memory_message_generated.h"

using namespace std;
using namespace DataBus::Task;
using namespace DataBus::Common;
using namespace DataBus::Common::Utils;

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
        DataBus::logger.Info("New task comes in, type {}, size {} from client {}",
            static_cast<int32_t>(task->Type()), task->Size(), task->ClientFd());
        // 读取消息体的大小和类型
        switch (task->Type()) {
            case Task::TaskType::OPEN: {
                connectionMgrPtr_->AddNewConnection(task->ClientFd());
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
                HandleClose(task->ClientFd());
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
        DataBus::logger.Error("Writing to client {} failed, data size={}", task.ClientFd(), task.Size());
        taskLoopPtr_->AddCloseTask(task.ClientFd());
    }
}

void TaskHandler::HandleRead(const Task& task)
{
    const size_t len = task.Size();
    const char* buffer = task.DataRaw();
    const int socketFd = task.ClientFd();

    if (len < MESSAGE_HEADER_LEN) {
        logger.Error("[TaskHandler] Incorrect message header length from client {}", socketFd);
        Utils::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    // 验证buf是否包含有效的消息头
    flatbuffers::Verifier verifier(reinterpret_cast<const uint8_t*>(buffer), MESSAGE_HEADER_LEN);
    if (!Common::VerifyMessageHeaderBuffer(verifier)) {
        logger.Error("[TaskHandler] Incorrect message header format from client {}", socketFd);
        Utils::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
        return;
    }

    auto header = Common::GetMessageHeader(buffer);

    // TODO: 需要处理半包和粘包
    uint bodySize = header->size();
    if (len < bodySize + MESSAGE_HEADER_LEN) {
        logger.Error("[TaskHandler] Incorrect message body length from client {}", socketFd);
        Utils::SendErrorMessage(ErrorType::IllegalMessageBody, GetSender(socketFd));
        return;
    }
    HandleMessage(header, buffer, socketFd);
}

void TaskHandler::HandleClose(int socketFd)
{
    // 关闭连接
    connectionMgrPtr_->CloseConnection(socketFd);

    auto permissionsHeld = resourceMgrPtr_->GetPermissionsHeld(socketFd);
    // 释放关闭连接的客户端当前所持有的权限
    if (!permissionsHeld.empty()) {
        logger.Info("[TaskHandler] Releasing permissions currently held by client {}", socketFd);
        for (auto& permissionHeld : permissionsHeld) {
            ReleasePermission(socketFd, permissionHeld.sharedMemoryId_, permissionHeld.permissionType_);
        }
    }
    // 把客户端从授权等待队列中移除
    if (!resourceMgrPtr_->GetWaitingPermitMemoryBlocks(socketFd).empty()) {
        logger.Info("[TaskHandler] Removing client {} from waiting permit queues", socketFd);
        resourceMgrPtr_->RemoveClientFromWaitingQueue(socketFd);
    }
}

void TaskHandler::HandleMessage(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 读取消息体的大小和类型
    switch (header->type()) {
        case Common::MessageType::HeartBeat: {
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
        case Common::MessageType::ReleaseMemory: {
            HandleMessageReleaseMemory(header, buffer, socketFd);
            break;
        }
        case Common::MessageType::GetMetaData: {
            HandleMessageGetMeta(header, buffer, socketFd);
            break;
        }
        case Common::MessageType::CleanupExpiredMemory: {
            HandleMessageCleanupExpiredMemory();
            break;
        }
        default:
            logger.Error("[TaskHandler] Unknown message type from client {}", socketFd);
            Utils::SendErrorMessage(ErrorType::IllegalMessageHeader, GetSender(socketFd));
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
        return;
    }
    auto applyMemoryMessage = Common::GetApplyMemoryMessage(startPtr);
    auto objectKey = applyMemoryMessage->object_key() ? applyMemoryMessage->object_key()->str() : "";
    logger.Info("[TaskHandler] Received ApplyMemory from client {}, object key: {}, size: {}", socketFd,
        objectKey, applyMemoryMessage->memory_size());

    // 如果申请内存大小为0，仅在资源管理模块标记，不实际分配内存。
    if (!objectKey.empty() && applyMemoryMessage->memory_size() == 0) {
        HandleApplyZeroMemory(socketFd, objectKey);
        return;
    }

    auto applyMemoryRes =
        resourceMgrPtr_->HandleApplyMemory(socketFd, objectKey, applyMemoryMessage->memory_size());
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
        SendApplyPermissionResponse({false, socketFd, -1, 0,
                                     make_shared<Resource::UserData>(), ErrorType::IllegalMessageBody});
        return;
    }
    auto applyPermissionMessage = Common::GetApplyPermissionMessage(startPtr);

    logger.Info("[TaskHandler] Received ApplyPermission from client {}, permission: {}", socketFd,
                static_cast<int32_t>(applyPermissionMessage->permission()));

    // 0大小内存权限申请
    if (applyPermissionMessage->object_key() &&
        resourceMgrPtr_->IsZeroMemory(applyPermissionMessage->object_key()->str())) {
        HandleApplyZeroMemoryPermission(socketFd, const_cast<ApplyPermissionMessage *>(applyPermissionMessage));
        return;
    }

    int32_t sharedMemoryId = applyPermissionMessage->memory_key() != -1 ? applyPermissionMessage->memory_key() :
                resourceMgrPtr_->GetMemoryId(applyPermissionMessage->object_key()->str());
    if (sharedMemoryId == -1) {
        logger.Error("[TaskHandler] The object key {} is not found", applyPermissionMessage->object_key()->str());
        SendApplyPermissionResponse({false, socketFd, -1, 0,
                                     make_shared<Resource::UserData>(), ErrorType::KeyNotFound});
        return;
    }

    const int8_t* userData = nullptr;
    size_t dataSize = 0;
    if (applyPermissionMessage->user_data()) {
        userData = applyPermissionMessage->user_data()->data();
        dataSize = applyPermissionMessage->user_data()->size();
    }
    auto userDataPtr = make_shared<Resource::UserData>(userData, dataSize);
    auto applyPermitResp =
            resourceMgrPtr_->HandleApplyPermission({socketFd, applyPermissionMessage->permission(),
                                                    sharedMemoryId, applyPermissionMessage->is_operating_user_data(),
                                                    userDataPtr});
    // 权限申请请求进入等待队列，阻塞客户端通知
    if (!applyPermitResp.granted_ && applyPermitResp.errorType_ == ErrorType::None) {
        return;
    }
    SendApplyPermissionResponse(applyPermitResp);
}

void TaskHandler::HandleMessageReleasePermission(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 解析消息体
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyReleasePermissionMessageBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect release permission body format");
        return;
    }
    auto releasePermissionMessage = Common::GetReleasePermissionMessage(startPtr);
    logger.Info("[TaskHandler] Received ReleasePermission from client {}, permission: {}", socketFd,
                static_cast<int32_t>(releasePermissionMessage->permission()));

    // 0大小内存权限释放,直接返回。
    if (releasePermissionMessage->object_key() &&
        resourceMgrPtr_->IsZeroMemory(releasePermissionMessage->object_key()->str())) {
        return;
    }

    int32_t sharedMemoryId = releasePermissionMessage->memory_key() != -1 ? releasePermissionMessage->memory_key() :
                             resourceMgrPtr_->GetMemoryId(releasePermissionMessage->object_key()->str());
    if (sharedMemoryId == -1) {
        logger.Error("[TaskHandler] The object key {} is not found", releasePermissionMessage->object_key()->str());
        return;
    }
    ReleasePermission(socketFd, sharedMemoryId, releasePermissionMessage->permission());
}

void TaskHandler::ReleasePermission(int32_t socketFd, int32_t sharedMemoryId, Common::PermissionType permissionType)
{
    if (!resourceMgrPtr_->HandleReleasePermission(socketFd, permissionType, sharedMemoryId)) {
        logger.Error("[TaskHandler] Failed to ReleasePermission, client: {}, permission: {}", socketFd,
                     static_cast<int32_t>(permissionType));
        return;
    }
    // 通知结束等待的客户端权限申请成功
    auto notificationQueue = resourceMgrPtr_->ProcessWaitingPermitRequests(sharedMemoryId);
    for (const auto& notification: notificationQueue) {
        SendApplyPermissionResponse(notification);
    }
    // 处理待释放的内存块
    if (!resourceMgrPtr_->ProcessPendingReleaseMemory(sharedMemoryId)) {
        logger.Error("[TaskHandler] Failed to ProcessPendingReleaseMemory for the memory key {}", sharedMemoryId);
    }
}

void TaskHandler::HandleMessageReleaseMemory(const Common::MessageHeader *header, const char *buffer, int socketFd)
{
    // 解析消息体
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyReleaseMemoryMessageBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect release memory body format");
        return;
    }
    auto releaseMemoryMessage = Common::GetReleaseMemoryMessage(startPtr);
    int32_t sharedMemoryId = releaseMemoryMessage->memory_key() != -1 ? releaseMemoryMessage->memory_key() :
                             resourceMgrPtr_->GetMemoryId(releaseMemoryMessage->object_key()->str());
    logger.Info("[TaskHandler] Received ReleaseMemory from client {}, memory key: {}", socketFd,
        sharedMemoryId);

    // 0大小内存释放，移除内存对应的用户自定义元数据。
    if (releaseMemoryMessage->object_key() &&
        resourceMgrPtr_->IsZeroMemory(releaseMemoryMessage->object_key()->str())) {
        resourceMgrPtr_->RemoveZeroMemoryUserData(releaseMemoryMessage->object_key()->str());
        return;
    }

    if (sharedMemoryId == -1) {
        logger.Error("[TaskHandler] The object key {} is not found", releaseMemoryMessage->object_key()->str());
        return;
    }
    if (!resourceMgrPtr_->HandleReleaseMemory(sharedMemoryId)) {
        logger.Error("[TaskHandler] Failed to ReleaseMemory, client: {}, memory key: {}",
            socketFd, sharedMemoryId);
    }
}

void TaskHandler::HandleMessageCleanupExpiredMemory()
{
    logger.Info("[TaskHandler] Clean up expired memory blocks");
    resourceMgrPtr_->CleanupExpiredMemory();
}

void TaskHandler::SendApplyMemoryResponse(int32_t socketFd, int32_t memoryId, uint64_t memorySize,
                                          ErrorType errorType)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    auto respBody = Common::CreateApplyMemoryMessageResponse(bodyBuilder, errorType, memoryId, memorySize);
    bodyBuilder.Finish(respBody);
    Utils::SendMessage(bodyBuilder, Common::MessageType::ApplyMemory, GetSender(socketFd));
    logger.Info("[TaskHandler] Send memory to client {}, result: {}, memory key: {}, size: {}",
        socketFd, static_cast<int32_t>(errorType), memoryId, memorySize);
}

void TaskHandler::SendGetMetaDataResponse(int32_t socketFd, ErrorType errorType, int32_t memoryId, uint64_t memorySize,
                                          const shared_ptr<Resource::UserData>& userData)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<::flatbuffers::Vector<int8_t>> userDataOffset = 0;
    if (userData && userData->userDataPtr_) {
        userDataOffset = bodyBuilder.CreateVector(userData->userDataPtr_.get(), userData->dataSize_);
    }
    auto respBody = Common::CreateGetMetaDataMessageResponse(
        bodyBuilder, errorType, userDataOffset, memoryId, memorySize);
    bodyBuilder.Finish(respBody);
    Utils::SendMessage(bodyBuilder, Common::MessageType::GetMetaData, GetSender(socketFd));
    logger.Info("[TaskHandler] Send metadata to client {}, result: {}, memory key: {}",
        socketFd, static_cast<int32_t>(errorType), memoryId);
}

void TaskHandler::SendApplyPermissionResponse(const Resource::ApplyPermissionResponse& response)
{
    flatbuffers::FlatBufferBuilder bodyBuilder;
    flatbuffers::Offset<::flatbuffers::Vector<int8_t>> userDataOffset = 0;
    if (response.userData_ && response.userData_->userDataPtr_) {
        userDataOffset =
                bodyBuilder.CreateVector(response.userData_->userDataPtr_.get(), response.userData_->dataSize_);
    }
    auto respBody =
        Common::CreateApplyPermissionMessageResponse(bodyBuilder, response.errorType_, response.granted_,
                                                     response.sharedMemoryId_, response.memorySize_, userDataOffset);

    bodyBuilder.Finish(respBody);
    Utils::SendMessage(bodyBuilder, Common::MessageType::ApplyPermission, GetSender(response.applicant_));
    logger.Info("[TaskHandler] Send Permission result to client {}, result: {}, memory key: {}, size: {}",
        response.applicant_, response.granted_, response.sharedMemoryId_, response.memorySize_);
}

std::function<void(const uint8_t*, size_t)> TaskHandler::GetSender(int32_t socketFd)
{
    return [this, socketFd](const uint8_t* buf, ssize_t s) {
        taskLoopPtr_->AddWriteTask(socketFd, reinterpret_cast<const char*>(buf), s);
    };
}

void TaskHandler::HandleMessageGetMeta(const Common::MessageHeader* header, const char* buffer, int socketFd)
{
    // 解析消息体。
    auto startPtr = buffer + MESSAGE_HEADER_LEN;
    flatbuffers::Verifier bodyVerifier(reinterpret_cast<const uint8_t*>(startPtr), header->size());
    if (!Common::VerifyGetMetaDataMessageBuffer(bodyVerifier)) {
        logger.Error("[TaskHandler] Received incorrect get meta body format");
        SendGetMetaDataResponse(socketFd, ErrorType::IllegalMessageBody, -1, 0,
                                make_shared<Resource::UserData>());
        return;
    }

    // 获取共享内存 ID。
    auto getMetaDataMessage = Common::GetGetMetaDataMessage(startPtr);
    auto objectKey = getMetaDataMessage->object_key() ? getMetaDataMessage->object_key()->str() : "";
    logger.Info("[TaskHandler] Received GetMetaData from client {}, object key: {}", socketFd, objectKey);

    // 0大小内存读取用户元数据
    if (resourceMgrPtr_->IsZeroMemory(objectKey)) {
        SendGetMetaDataResponse(socketFd, ErrorType::None, -1, 0,
                                resourceMgrPtr_->GetZeroMemoryUserData(objectKey));
        return;
    }

    auto sharedMemoryId = resourceMgrPtr_->GetMemoryId(objectKey);
    if (sharedMemoryId == -1) {
        logger.Error("[GetMetaData] The object key {} is not found from client {}", objectKey, socketFd);
        SendGetMetaDataResponse(socketFd, ErrorType::KeyNotFound, -1, 0,
                                make_shared<Resource::UserData>());
        return;
    }
    uint64_t memorySize = resourceMgrPtr_->GetMemorySize(sharedMemoryId);
    auto userData = resourceMgrPtr_->GetUserData(sharedMemoryId);
    SendGetMetaDataResponse(socketFd, ErrorType::None, sharedMemoryId, memorySize, userData);
}

void TaskHandler::HandleApplyZeroMemory(int32_t socketFd, const std::string& objectKey)
{
    if (resourceMgrPtr_->IsZeroMemory(objectKey)) {
        logger.Error("[TaskHandler] The object key {} already exists", objectKey);
        SendApplyMemoryResponse(socketFd, -1, 0, ErrorType::KeyAlreadyExists);
    } else {
        resourceMgrPtr_->AddZeroMemoryUserData(objectKey, make_shared<Resource::UserData>());
        SendApplyMemoryResponse(socketFd, -1, 0, ErrorType::None);
    }
}

void TaskHandler::HandleApplyZeroMemoryPermission(int32_t socketFd,
                                                  Common::ApplyPermissionMessage* applyPermissionMessage)
{
    const std::string& objectKey = applyPermissionMessage->object_key()->str();
    const PermissionType& permissionType = applyPermissionMessage->permission();
    const bool isOperatingUserData = applyPermissionMessage->is_operating_user_data();
    // 需要更新用户自定义元数据
    if (permissionType == PermissionType::Write && isOperatingUserData) {
        const int8_t* userData = nullptr;
        size_t dataSize = 0;
        if (applyPermissionMessage->user_data()) {
            userData = applyPermissionMessage->user_data()->data();
            dataSize = applyPermissionMessage->user_data()->size();
        }
        auto userDataPtr = make_shared<Resource::UserData>(userData, dataSize);
        resourceMgrPtr_->AddZeroMemoryUserData(objectKey, userDataPtr);
    }
    // 当且仅当读请求且设定操作用户自定义数据时，返回用户自定义元数据
    auto userDataPtr = permissionType == PermissionType::Read && isOperatingUserData ?
                       resourceMgrPtr_->GetZeroMemoryUserData(objectKey) : make_shared<Resource::UserData>();
    SendApplyPermissionResponse({true, socketFd, -1, 0, userDataPtr, ErrorType::None});
}
}  // Task
}  // DataBus
